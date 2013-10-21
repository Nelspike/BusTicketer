var express = require('express'),
	app = express(),
	dbLib = require('./tickets'),
	db = new dbLib.db("tickets.db"),
	crypto=require('crypto'),
	port = 81;	// Porta por defeito
	
//configuracao
app.configure(function() {
	app.use(express.bodyParser());
	app.use(function (req, res, next) {
	    res.setHeader('Server', 'TicketSystem');

	    return next();
	});

	app.enable('trust proxy');
	app.disable('x-powered-by');

	app.use('/', app.router);
});

var args = process.argv.splice(2),
	p = null;

if (args.length > 0) {
	p = parseInt(args[0]);
	if( p )
		port = p ;

	p = null;
}


console.log('Escuta na porta: ' + port)
app.listen(port);


// Funcoes

function respondToJSON(req, res, out, statusCode) {
	var size;

	out = JSON.stringify( out );
	size = Buffer.byteLength( out, 'UTF-8' );

	res.writeHead( statusCode,
				   { 'Content-Type': 'application/json; charset=utf-8',
					 'Content-Length': size} );

	res.write( out );
	res.end();
}




/*
 *	Routes
 */
 
 
//Criar um cliente
//POST PARAMS: name string,nib string,pass string,cardType string, validity string
//RETURN JSON {name:<clientname>,id:<clientid>}
app.post('/client/create',function (req,res) {
	//console.log(req);
	var client = new dbLib.Client(req.body.name);
	if( !req.body.name || !req.body.nib ||  !req.body.cardType || !req.body.validity ||!req.body.pass) {
		var out = {};
		out.error = "Bad request";
		respondToJSON( req, res, out, 400 );
		
	}
	else
	{
		client.nib = req.body.nib;
		client.cardType=req.body.cardType;
		client.validity=req.body.validity;
		db.createClient(client,req.body.pass, function(err, lastID, row) {
			var out = {},
				code;

			if( err ) {
				code = 500;
				out.id = -1;
				out.error = 'Impossible to add client';

				console.log('Error adding client: ' + err);
			}
			else {
				code = 200;
				out.id = lastID;
				out.name=client.name;
				console.log('Added new client: ' + lastID+' '+client.name);
			}

			respondToJSON( req, res, out, code );
		
		});
	}

});

//logar um cliente
//POST PARAMS: name string,pass string
//RETURN JSON {name:<clientname>,id:<clientid>}
app.post('/client/login',function (req,res) {
	if( !req.body.name ||!req.body.pass)
		respondToJSON( req, res, {error: 'Bad request'}, 400 );
	else
	{
		var name=req.body.name,pass=req.body.pass;
		db.login(name,pass, function(err,row) {
			var out = {},
				code;

			if( err ) {
				code = 500;
				out.id = -1;
				out.error = 'Impossible to add client';

				console.log('Error login client: ' + err);
			}
			else {
				code = 200;
				if (!row)
				{
					out.id = -1;
					out.error = 'Wrong user or password';
					
					console.log('Fail login');
				}
				else
				{
				
					out.id=row.cid;
					out.name=row.name;
					console.log('Logged in : ' +name);
				}
			}

			respondToJSON( req, res, out, code );
		
		});
	}

});


//validar um bilhete
//POST /validate PARAMS: cid:client id, tid:ticket id, bid:bus id
//Return JSON {status:true/false}
app.post('/validate',function(req,res){
	if( !req.body.tid ||!req.body.cid||!req.body.bid)
		respondToJSON( req, res, {error: 'Bad request'}, 400 );
	else
	{
		var cid=req.body.cid,tid=req.body.tid,bid=req.body.bid;
		db.validate(cid,bid,tid, function(err,row) {
			var out = {},
				code;

			if( err ) {
				code = 500;
				out.error = 'Impossible to add client';
				out.status=false;
				console.log('Error validating ticket: ' + err);
			}
			else {
				code = 200;
				if (!row)
				{
					out.error = 'Wrong TICKET/USER';
					out.status=false;
					console.log('Fail validate: ',tid,' ',cid,' ',bid);
				}
				else
				{
				
					out.status=true;
					console.log('validate ticket: ',tid,' ',cid,' ',bid);
				}
			}

			respondToJSON( req, res, out, code );
		
		});
	}
});
 
 // Retorna bilhetes de um cliente
 // GET /list/:clientID PARAMS clientID: numeric client ID
 //returns {t1:21,t2:32,t3:43}
app.get('/list/:client', function (req, res) {

	var cid = req.params.client;

	// Verifica se todos os valores enviados sÃ£o inteiros e maiores que zero
	// NÃ£o verifica se os cÃ³digos enviados existem na base de dados
	if( !cid )
		respondToJSON( req, res, {error: 'Bad request'}, 400 );

	else {

		db.listTickets(cid, function(err,result) {
			var out = {},
				code;

			if( err ) {
				code = 500;
				out.error = 'Impossible to list tickets for client';
				console.log('Error listing tickets: ' + err);
			}
			else {
				code = 200;
				if (!result)
				{
					out.error = 'Wrong user';
					console.log('Fail list tickets: ',cid);
				}
				else
				{
				
					out=result;
					console.log('client tickets: ',cid, ' ',JSON.stringify( out ));
				}
			}

			respondToJSON( req, res, out, code );
		
		});
		
	}
});


// Comprar bilhetes
// POST /buy PARAMS: cid:client id, t1:nr de t1s, t2:nr de t2s,t3:nr de t3s
// returns {t1:21,t2:32,t3:43}
app.post('/buy', function (req, res) {
//TODO
	var cid = req.params.client,t1=req.params.t1,t2=req.params.t2,t3=req.params.t3;
	
	// Verifica se todos os valores enviados sÃ£o inteiros e maiores que zero
	// NÃ£o verifica se os cÃ³digos enviados existem na base de dados
	if( !cid ||!t1||!t2||!t3)
		respondToJSON( req, res, {error: 'Bad request'}, 400 );

	else {

		db.buyTickets(cid,t1,t2,t3, function(err,result) {
			var out = {},
				code;

			if( err ) {
				code = 500;
				out.error = 'Impossible to list tickets for client';
				console.log('Error listing tickets: ' + err);
			}
			else {
				code = 200;
				if (!row)
				{
					out.error = 'Wrong user';
					console.log('Fail list tickets: ',cid);
				}
				else
				{
				
					out=result;
					console.log('client tickets: ',cid, ' ',JSON.stringify( out ));
				}
			}

			respondToJSON( req, res, out, code );
		
		});
		
	}
});


// Multar client
// POST /fine PARAMS: cid:client id
// returns {status:false/true}
app.post('/fine', function (req, res) {
//TODO
	var cid = req.body.client;

	// Verifica se todos os valores enviados sÃ£o inteiros e maiores que zero
	// NÃ£o verifica se os cÃ³digos enviados existem na base de dados
	if( !cid )
		respondToJSON( req, res, {error: 'Bad request'}, 400 );

	else {

		db.listTickets(cid, function(err,result) {
			var out = {},
				code;

			if( err ) {
				code = 500;
				out.error = 'Impossible to list tickets for client';
				console.log('Error listing tickets: ' + err);
			}
			else {
				code = 200;
				if (!row)
				{
					out.error = 'Wrong user';
					console.log('Fail list tickets: ',cid);
				}
				else
				{
				
					out=result;
					console.log('client tickets: ',cid, ' ',JSON.stringify( out ));
				}
			}

			respondToJSON( req, res, out, code );
		
		});
		
	}
});

 app.all('*', function (req, res) {

	console.log('Pedido não encontrado: ' + req.path + " [" + req.method + "]");

	respondToJSON( req, res, { error: 'Página não encontrada'}, 404 );
});


/*
 *	CONSOLE INPUT (TEST PURPOSE;REMOVE LATER)
 */
/* 
 var readline = require('readline');

var rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
});

rl.question("reading input\n", function(answer) {
  
  console.log(answer );
  if (answer=="versions") console.log(crypto.getHashes());
  if (answer=="exit")rl.close();
});

*/
