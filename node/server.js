var express = require('express'),
	app = express(),
	TicketsLib = require('./tickets'),
	tickets = new TicketsLib(),
	port = 81;	// Porta por defeito
	
//configuracao
app.configure(function() {

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

if (args.length > 0)
{
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
 
 // Retorna bilhetes de um cliente
app.get('/list/:client', function (req, res) {

	var client = req.params.client;

	// Verifica se todos os valores enviados são inteiros e maiores que zero
	// Não verifica se os códigos enviados existem na base de dados
	if( !client )
		respondToJSON( req, res, {error: 'Bad request'}, 400 );

	else
	{
		var out=ticket.list(client);
		respondToJSON( req, res, out, 200 );
		
	}
});
 
 
 app.all('*', function (req, res) {

	console.log('Pedido não encontrado: ' + req.path + " [" + req.method + "]");

	respondToJSON( req, res, { error: 'Página não encontrada'}, 404 );
});




