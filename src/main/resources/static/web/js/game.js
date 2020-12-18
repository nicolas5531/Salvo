var urlParams = new URLSearchParams(window.location.search);
var gp = urlParams.get('gp');
var api = '/api/game_view/' + gp;

var app = new Vue({
    el: "#app",
    data: {
        gp: null,
        columns: [1, 2, 3, 4,5, 6, 7, 8, 9, 10] ,
        rows: ["A", "B", "C", "D", "E", "F", "G", "H","I", "J"] ,
        gameView: null,
        vs: null,
        viewer: null,
        ships: [],
        position: "vertical",
        shipSelected: "",
        shipProvisionalPosition: null,
        row: null,
        col:null,
        newGame:"",
        salvoes: {"turn": 0, "locations":[]},
    },
    methods: {

        refreshPage: function(){                                //recarga pagina si esta esperando que dispare oponente

            if(app.gameView != null && (app.gameView.status == 'WAIT' || app.gameView.status == 'WAIT_OPPONENT')){
                setTimeout(function(){document.location.reload()},10000);

            }
        },

        newShip: function(row, column){
            if(app.shipSelected == ""){
                alert("Please select ships and position");
            }
            else{
                app.row = row;
                app.col = column;
                if((app.ships.findIndex(ship => ship.type === app.shipSelected) != -1)){        //ship representa el index => shipe.type la propiedad a la que quiero accedeer para comparar
                    app.borraShips();                                                           //  Check si la nave ya esta colocada
                    app.dibujaShips();
                }
                else{
                    app.dibujaShips();
                }
            }
        },
        newSalvo: function(row, column){
            app.row = row;
            app.col = column;
            let index = app.salvoes.locations.indexOf(row + column);
            let playerSalvoes = app.gameView.salvoes.filter(salvo => salvo.player == app.viewer.id);

            if(app.salvoes.locations.includes(row + column) == true){
                document.getElementById(row + column + 's').classList.remove('salvo');
                app.salvoes.locations.splice(index,1);
            }
            else if(playerSalvoes.findIndex(salvo => salvo.salvoLocations.includes(row + column)) != -1 ){
                alert("You already have a shot here")
            }
            else if(app.salvoes.locations.length < 5){
                document.getElementById(row + column + 's').classList.add('salvo');
                app.salvoes.locations.push(row + column);
            }
            else{alert("YOU JUST HAVE 5 SHOTS")}
        },

        calcularTurno: function(){
            let playerSalvoes = app.gameView.salvoes.filter(salvo => salvo.player == app.viewer.id);
            app.salvoes.turn = playerSalvoes.length +1;
        },

        borraShips: function(){
            let celdas = 0;
            if(app.shipSelected == "Carrier"){
                celdas = 5;
            }
            if(app.shipSelected == "Battleship"){
                celdas = 4;
            }
            if(app.shipSelected == "Submarine"){
                celdas = 3;
            }
            if(app.shipSelected == "Destroyer"){
                celdas = 3;
            }
            if(app.shipSelected == "Patrol Boat"){
                celdas = 2;
            }
            if(app.position == "horizontal"){
                var ship = {"type": app.shipSelected, "locations":[]};
                var index = app.ships.findIndex(shipp => shipp.type === app.shipSelected);
                for(var i = 0; i < app.ships[index].locations.length ; i++){
                    document.getElementById(app.ships[index].locations[i]).classList.remove('ship');
                }
                app.ships.splice(index,1);      //elimino 1 array en la posicion dada por index
            }
            if(app.position == "vertical"){
                var ship = {"type": app.shipSelected, "locations":[]};
                var index = app.ships.findIndex(ship => ship.type === app.shipSelected);
                for(var i = 0; i < app.ships[index].locations.length ; i++){
                    document.getElementById(app.ships[index].locations[i]).classList.remove('ship');
                }
                app.ships.splice(index,1);
            }
        },

        dibujaShips: function(){
            let celdas = 0;
            if(app.shipSelected == "Carrier"){
                celdas = 5;
            }
            else if(app.shipSelected == "Battleship"){
                celdas = 4;
            }
            else if(app.shipSelected == "Submarine"){
                celdas = 3;
            }
            else if(app.shipSelected == "Destroyer"){
                celdas = 3;
            }
            else if(app.shipSelected == "Patrol Boat"){
                celdas = 2;
            }
            if(app.position == "horizontal"){
                var ship = {"type": app.shipSelected, "locations":[]};
                if(app.col + celdas <= 11){
                     for(var i = 0; i < celdas; i++){
                        ship.locations.push(app.row + app.col);
                        app.col++;
                    }
                    var index = app.ships.findIndex(nave => nave.locations.findIndex(loc => ship.locations.includes(loc)) >= 0 )   //id de la Nave que se intercepta con otra
                    if(index != -1){
                        alert("You 've another ship in that place");
                    }
                    else{
                        for(var i = 0; i < ship.locations.length; i++){
                            document.getElementById(ship.locations[i]).classList.add('ship');
                        }
                        app.ships.push(ship);
                    }
                }
                else{
                    alert("You are out of the grid my friend!")
                }
            }
            else if(app.position == "vertical"){
                var ship = {"type": app.shipSelected, "locations":[]};
                var indice = app.rows.indexOf(app.row);
                if((indice + 1) + celdas <= 11){
                    for(var i = 0; i < celdas; i++){
                        ship.locations.push(app.rows[indice] + app.col);
                        indice++;
                    }
                    var index = app.ships.findIndex(nave => nave.locations.findIndex(loc => ship.locations.includes(loc)) >= 0 )   //id de la Nave que se intercepta con otra
                    if(index != -1){
                        alert("You 've another ship in that place");
                    }
                    else{
                        for(var i = 0; i < ship.locations.length; i++){
                            document.getElementById(ship.locations[i]).classList.add('ship');
                        }
                        app.ships.push(ship);
                    }
                }
                else{
                    alert("You are out of the grid my friend!")
                }
            }
        },
        addSalvoes: function(gpId){
            app.calcularTurno();
            $.post({
              url: "/api/games/players/"+gpId+"/salvoes",
              data: JSON.stringify(app.salvoes),
              dataType: "text",
              contentType: "application/json",

            })
            .done(function (response) {
                alert( response );
                location.reload();
            })
            .fail(function (error) {
                alert("Failed to add Salvoes:" + error.responseText);
            })
        },
        addShips: function(gpId){
            $.post({
              url: "/api/games/players/"+gpId+"/ships",
              data: JSON.stringify(app.ships),
              dataType: "text",
              contentType: "application/json",

            })
            .done(function (response) {
                alert( "Ship added " );
                location.reload()
            })
            .fail(function (error) {
                alert("Failed to add Ship:" + error.responseText);
            })
        },

        logOut: function(){
            $.post("/api/logout").done(function() { window.location ="http://localhost:8080/web/games.html" })
        },

        drawShips: function(){
            for(var i = 0; i < app.gameView.ships.length; i++){
                for(var t = 0; t < app.gameView.ships[i].shipLocation.length; t++){
                    document.getElementById(app.gameView.ships[i].shipLocation[t]).classList.add('ship');
                }
            }
        },

         isHit: function(salvoLocation) {
            var hitFound = false;
            for (var y = 0; y < app.gameView.ships.length; y++) {
                for (var q = 0; q < app.gameView.ships[y].shipLocation.length; q++) {
                    if (salvoLocation == app.gameView.ships[y].shipLocation[q]) {
                        hitFound = true;
                    }
                }
            }
            return hitFound;
        },

        shipLeft: function(){
            for(var i = 0; i < app.gameView.sunken.length; i++){
                for(var t = 0; t < app.gameView.sunken[i].sunken.length; t++){
                    if(app.gameView.sunken != null && app.gameView.sunken[i].sunken[t].type == "Carrier" ){
                        document.getElementById("1").classList.add('salvoDamage');
                    }
                    if(app.gameView.sunken != null && app.gameView.sunken[i].sunken[t].type == "Battleship" ){
                        document.getElementById("2").classList.add('salvoDamage');
                    }
                    if(app.gameView.sunken != null && app.gameView.sunken[i].sunken[t].type == "Destroyer" ){
                        document.getElementById("3").classList.add('salvoDamage');
                    }
                    if(app.gameView.sunken != null && app.gameView.sunken[i].sunken[t].type == "Submarine" ){
                        document.getElementById("4").classList.add('salvoDamage');
                    }
                    if(app.gameView.sunken != null && app.gameView.sunken[i].sunken[t].type == "Patrol Boat" ){
                        document.getElementById("5").classList.add('salvoDamage');
                    }
                }
            }
        },

        drawSunkViewer: function(){
            for(var i = 0; i < app.gameView.sunken.length; i++){
                for(var t = 0; t < app.gameView.sunken[i].sunken.length; t++){
                    for(var k = 0; k < app.gameView.sunken[i].sunken[t].shipLocation.length; k++){
                        document.getElementById(app.gameView.sunken[i].sunken[t].shipLocation[k] + "s").classList.remove('salvoHit');
                        document.getElementById(app.gameView.sunken[i].sunken[t].shipLocation[k] + "s").classList.add('salvoSunken');
                    }
                }
            }
        },

        drawHitViewer: function(){
            for(var i = 0; i < app.gameView.hits.length; i++){
                for(var t = 0; t < app.gameView.hits[i].hits.length; t++){
                    document.getElementById(app.gameView.hits[i].hits[t] + "s").classList.remove('salvoWater');
                    document.getElementById(app.gameView.hits[i].hits[t] + "s").classList.add('salvoHit');
                }
            }
        },

        drawSalvoesVs: function(){
            for(var i = 0; i < app.gameView.salvoes.length; i++){
                for(var t = 0; t < app.gameView.salvoes[i].salvoLocations.length; t++){
                    if(app.gameView.salvoes[i].player != app.viewer.id){        //oponente
                         if(app.isHit(app.gameView.salvoes[i].salvoLocations[t])){
                             document.getElementById(app.gameView.salvoes[i].salvoLocations[t]).classList.add('salvoHit');
                         }
                         else{
                             document.getElementById(app.gameView.salvoes[i].salvoLocations[t]).classList.add('salvoWater');
                         }
                    }
                    else{                                                       //jugador
                         document.getElementById(app.gameView.salvoes[i].salvoLocations[t] + 's').classList.add('salvoWater');
                    }
                }
            }
        },

        gameInformation: function(){
            for(var i = 0; i < app.gameView.gamePlayers.length; i++){
                if (app.gp == app.gameView.gamePlayers[i].gpId){
                    app.viewer = app.gameView.gamePlayers[i].player || "???";
                }
                else{
                    app.vs = app.gameView.gamePlayers[i].player || "???";
                }
            }
        },
    },

    filters: {
      formatDate: function (value) {
        if (!value) return ''           //valida
        return moment(value).format('DD/MM/YYYY, h:mm:ss A');
      }
    },
});

fetch(api, {
        method: 'GET',
        headers: {
        }
    }).then(function (response) {
    console.log(response)
        if (response.ok) {
            return response.json();
        }
        else{
            throw new Error(response.status)
        }
    })
    .then(function (data) {
        app.gameView = data.gp;
        app.gp = gp;
        app.gameInformation();
        app.drawSalvoesVs();
        app.drawHitViewer();
        app.drawSunkViewer();
        app.shipLeft();
        app.drawShips();
        app.refreshPage();
        app.newGame = app.gameView.ships;
    })
    .catch(function (error) {
        console.log('Looks like there was a problem: \n', error);
        alert("Shit happend Dude! You don't have acces");
        //window.location.href = "/web/games.html";

    });

