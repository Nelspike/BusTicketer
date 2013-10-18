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
			
				.run("CREATE TABLE clients (cid INTEGER PRIMARY KEY, name TEXT NOT NULL, nib TEXT NOT NULL, salt TEXT NOT NULL, pass TEXT NOT NULL);")
				.run("CREATE TABLE tickets (tid INTEGER PRIMARY KEY, type INTEGER NOT NULL,  cid REFERENCES clients(cid), INTEGER dateValidated TEXT NOT NULL, dateBought TEXT NOT NULL);")
				
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
		ticketConn.run("INSERT INTO clients (name,nib,salt,pass) VALUES (?,?,?,?);",
		[client.name,client.nib,salt,hashedPass],
		function(err){
			console.log(pass," ",salt," ",hashedPass);
			callback(err, this.lastID, this.changes);
		});
	});
}

sqliteDB.prototype.login=function(name,pass,callback)
{
	console.log("login client ",name);	
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

 
/*
 *	TICKET CLASS
 */

function Ticket(type,client){
	this.id=null;
	this.client=client;
	this.type=type;
	this.dateValidated="";
	this.dateBought="";
}

/*
 *	CLIENT CLASS
 */ 
 
function Client(name){
	this.id=null;
	this.name=name;
	this.tickets=[];
	this.nib=null;
}




/*
 *	Functions
 */

//get time
function timeNow()
{
	return ( new Date() / 1000 ) | 0 ;
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