var fs = require("fs"),
	moment = require('moment'),
    sqlite3 = require("sqlite3").verbose(),
    ticketConn = undefined,
	crypto=require('crypto');
module.exports.db=sqliteDB;
module.exports.Client=Client;
module.exports.Ticket=Ticket;

/*
 *	DATABASE
 */
 
function sqliteDB(file) {
    
	console.log("Opening file:"+file);
	var exists = fs.existsSync(file);

	if( !exists )
	{
		console.log("Creating DB file: " + file );
		fs.openSync(file, "w");
	}

	ticketConn = new sqlite3.Database(file,function() {
		ticketConn.run('PRAGMA foreign_keys=on');
    });

	if( !exists )
	{
		// Força que todas as queries dentro do serialize, e ao mesmo nível,
		// sejam executadas em série e não em paralelo.

		ticketConn.serialize(function(){

			ticketConn
				.run("BEGIN;")

				// Cria as tabelas
			
				.run("CREATE TABLE clients (cid INTEGER PRIMARY KEY, name TEXT NOT NULL, nib TEXT NOT NULL, cardType TEXT NOT NULL, validity TEXT NOT NULL, salt TEXT NOT NULL, pass TEXT NOT NULL);")
				.run("CREATE TABLE tickets (tid INTEGER PRIMARY KEY, type INTEGER NOT NULL,  cid REFERENCES clients(cid), dateValidated TEXT, dateBought TEXT NOT NULL, busId TEXT);")
				.run("CREATE TABLE multas (mid INTEGER PRIMARY KEY, cid REFERENCES clients(cid), dateInfraction TEXT NOT NULL);")
				// Insere os dados na db

				//.run("INSERT INTO clients (name) VALUES ('Diogo'), ('Nelson');")
				//.run("INSERT INTO tickets (type,dateValidated,dateBought) VALUES ('Cheese',);")
				
			.run("COMMIT;");
		});
	}
}

sqliteDB.prototype.createClient=function(client,pass,callback)
{
	console.log("create client ",client.name);
	if( typeof callback !== 'function')
		throw new Error('Callback is not a function');
	createHashPwd(pass,function(hashedPass,salt){
		ticketConn.run("INSERT INTO clients (name,nib,cardType,validity,salt,pass) VALUES (?,?,?,?,?,?);",
		[client.name,client.nib,client.cardType,client.validity,salt,hashedPass],
		function(err){
			console.log(pass," ",salt," ",hashedPass);
			callback(err, this.lastID, this.changes);
		});
	});
}

sqliteDB.prototype.login=function(name,pass,callback)
{
	console.log("try login client: ",name);	
	if( typeof callback !== 'function')
		throw new Error('Callback is not a function');
	ticketConn.all("SELECT cid,name,nib,salt,pass FROM clients WHERE name=?",
	[name],
	function(err, row) {
		if( row && row.length > 0 )
		{
			row=row[0];
			verifyPwd(pass,row.salt,row.pass,function(sucess){
				if (sucess)callback(err,row );
				else callback(err,null);
			});

		}
		else
			callback(err,null );
	});
}

sqliteDB.prototype.validate=function(type,client,bus,callback)
{
	console.log("validate ticket ",type," client ",client);
	if ( typeof callback !== 'function')
		throw new Error('Callback is not a function');
	ticketConn.get("SELECT tid FROM tickets WHERE type=? AND cid=? AND busID IS NULL",[type,client],
		function (err,row) {
			if (row)
			{
				//console.log(JSON.stringify(row));
				
				var date=timestamp();
				var ticket=row.tid;
				ticketConn.run("UPDATE tickets SET dateValidated=?,busID=? WHERE tid=?",[date,bus,ticket],
					function(err)
					{
						if (!err)console.log("validate db changes ",this.changes);
						else console.log("validate db error");
						callback(err,this.changes);
					});
			}
			else
				callback(err,null);
		});
	
}

sqliteDB.prototype.listTickets=function(clientID,callback)
{
	console.log("list tickets for ",clientID);
	if ( typeof callback !== 'function')
		throw new Error('Callback is not a function');
		
	var out={};out.t1=0;out.t2=0;out.t3=0;
	ticketConn.each("SELECT type FROM tickets WHERE cid=? AND busID is NULL",[clientID],
		function (err,row) { //eachfunction
			if (err)
			{
				console.log("each ticket error ",err);
			}
			else
			{
				if (row.type==1) out.t1++;
				if (row.type==2) out.t2++;
				if (row.type==3) out.t3++;
			}
		},
		function(err,nrows)
		{
			callback(err,out);
		});	
}

sqliteDB.prototype.getValidated=function(busId,callback)
{
	if ( typeof callback !== 'function')
		throw new Error('Callback is not a function');	
	
	console.log("get validated for ",busId);
	var out=[];
	var time1=moment().subtract('minutes',15).format("YYYY-MM-DDTHH:mm:ss");
	var time2=moment().subtract('minutes',30).format("YYYY-MM-DDTHH:mm:ss");
	var time3=moment().subtract('minutes',45).format("YYYY-MM-DDTHH:mm:ss");
	ticketConn.serialize(function(){
		console.log("x1");
		ticketConn.all("SELECT cid,dateValidated FROM tickets WHERE type=3 AND busID=? AND dateValidated>?",[busId,time3],
		function (err,row) {
			if (row && !err)
			{
				console.log("validated t3 ",JSON.stringify(row));
				for (var i=0;i<row.length;i++)
				{
					out.push(row[i].cid);
				}
			}
			else
				console.log("error get validated t3: ",err);
		});
		ticketConn.all("SELECT cid,dateValidated FROM tickets WHERE type=2 AND busID=? AND dateValidated>?",[busId,time2],
		function (err,row) {
			if (row && !err)
			{
				console.log("validated t2 ",JSON.stringify(row));
				for (var i=0;i<row.length;i++)
					out.push(row[i].cid);
			}
			else
				console.log("error get validated t2: ",err);
		});
		ticketConn.all("SELECT cid,dateValidated FROM tickets WHERE type=1 AND busID=? AND dateValidated>?",[busId,time1],
		function (err,row) {
			if (row && !err)
			{
			
				console.log("validated t1",JSON.stringify(row));
				for (var i=0;i<row.length;i++)
					out.push(row[i].cid);
			}
			else
				console.log("error get validated t1: ",err);
			callback(out);
		});
	});
}

sqliteDB.prototype.buyTickets=function(clientID,t1,t2,t3,callback)
{
	console.log("buy tickets for ",clientID);
	if ( typeof callback !== 'function')
		throw new Error('Callback is not a function');	
	var count,resto;
	resto=t3%10;
	count=t3-resto;
	t3+=count/10;
	count=resto+t2;
	resto=count%10;
	count=count-resto;
	t2+=count/10;
	count=resto+t1;
	resto=count%10;
	count=count-resto;
	t1+=count/10;
	var ts=timestamp();
	
	var out={};out.t1=0;out.t2=0;out.t3=0;
	console.log("tickets + bonus: ",t1," ",t2," ",t3);
	ticketConn.serialize(function(){
		ticketConn.parallelize(function(){
			for (var i=0;i<t3;i++)
			{
				ticketConn.run("INSERT INTO tickets (cid,type, dateBought) VALUES (?,3,?);",[clientID,ts]);
			}
			for (var j=0;j<t2;j++)
			{
				ticketConn.run("INSERT INTO tickets (cid,type, dateBought) VALUES (?,2,?);",[clientID,ts]);

			}
			for (var k=0;k<t1;k++)
			{
				ticketConn.run("INSERT INTO tickets (cid,type, dateBought) VALUES (?,1,?);",[clientID,ts]);

			}
		});
		ticketConn.each("SELECT type FROM tickets WHERE cid=?",[clientID],
			function (err,row) { //eachfunction
				if (err)
				{
					console.log("each ticket error ",err);
				}
				else
				{
					if (row.type==1) out.t1++;
					if (row.type==2) out.t2++;
					if (row.type==3) out.t3++;
				}
			},
			function(err,nrows)
			{
				callback(err,out);
			});	
	});
}
 
/*
 *	TICKET CLASS
 */

function Ticket(type,client){
	this.id=null;
	this.client=client;
	this.type=type;
	this.dateValidated="";
	this.dateBought="";
	this.bus=null;
}

/*
 *	CLIENT CLASS
 */ 
 
function Client(name){
	this.id=null;
	this.name=name;
	this.tickets=[];
	this.nib=null;
	this.cardType=null;
	this.validity=null;
}




/*
 *	Functions
 */

//get time
function timeNow()
{
	return ( new Date() / 1000 ) | 0 ;
}

function timestamp(){
	return moment().format("YYYY-MM-DDTHH:mm:ss");
}

//createHashPwd
//create a password salt and hash it.
//args: pass -> password string, callback->callback with password and salt argument
function createHashPwd(pass,callback)
{
	var salt=crypto.randomBytes(128).toString('base64');
	crypto.pbkdf2(pass, salt , 100, 512, function(err,hashPass){
		if (err) console.log("create hash error ",err);
		callback(hashPass.toString('base64'),salt.toString('base64'));
	});
}


function verifyPwd(pass,salt,hashed,callback)
{
	crypto.pbkdf2(pass, salt , 100, 512, function(err,hashPass){
		if (hashPass.toString('base64')==hashed&&!err)callback(true);
		else 
		{
			if (err) console.log("verify hash error ",err);
			callback(false);
		}
	});
}