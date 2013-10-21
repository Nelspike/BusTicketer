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
//POST PARAMS: name string,nib string,pass string
//RETURN JSON {name:<clientname>,id:<clientid>}
app.post('/client/create',function (req,res) {
	//console.log(req);
	var client = new dbLib.Client(req.body.name);
	if( !req.body.name || !req.body.nib || !req.body.pass) {
		var out = {};
		out.error = "Bad request";
		respondToJSON( req, res, out, 400 );
		
	}
	else
	{
		client.nib = req.body.nib;
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
					console.log('Login client: ' +name);
				}
			}

			respondToJSON( req, res, out, code );
		
		});
	}

});
 
 // Retorna bilhetes de um cliente
app.get('/list/:client', function (req, res) {

	var client = req.params.client;

	// Verifica se todos os valores enviados são inteiros e maiores que zero
	// Não verifica se os códigos enviados existem na base de dados
	if( !client )
		respondToJSON( req, res, {error: 'Bad request'}, 400 );

	else {
		
		var out = {};
		respondToJSON( req, res, out, 200 );
		
	}
});

 app.all('*', function (req, res) {

	console.log('Pedido n�o encontrado: ' + req.path + " [" + req.method + "]");

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
