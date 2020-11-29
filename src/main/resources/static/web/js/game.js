var urlParams = new URLSearchParams(window.location.search);
var gp = urlParams.get('gp');
var api = '/api/game_view/' + gp;

var app = new Vue({
    el: "#app",
    data: {
        gp: null,
        columns: ["1", "2", "3", "4","5", "6", "7", "8", "9", "10"] ,
        rows: ["A", "B", "C", "D", "E", "F", "G", "H","I", "J"] ,
        gameView: null,
        vs: null,
        viewer: null,
    },
    methods: {
        addShips: function(gpId){
            $.post({
              url: "/api/games/players/"+gpId+"/ships",
              data: JSON.stringify([
              {
              "type": "Patrol Boat",
              "locations":["H1", "H2"]},
              {"type": "Destroyer", "locations":["F1", "F2", "F3"]},
              {"type": "Submarine",
              "locations":["A2", "B2", "C2"]},
              {"type": "Battleship",
              "locations":["A8", "B8", "C8", "D8"]},
              {"type": "Carrier",
              "locations":["A10", "B10", "C10", "D10", "E10"]},
              ]),
              dataType: "text",
              contentType: "application/json"
            })
            .done(function () {
                location.reload()
                alert( "Ship added " + response );
            })
            .fail(function (jqXHR, status, httpError) {
                alert("Failed to add Ship: " + status + " " + httpError);
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
        
        drawSalvoesVs: function(){
            for(var i = 0; i < app.gameView.salvoes.length; i++){
                for(var t = 0; t < app.gameView.salvoes[i].salvoLocations.length; t++){
                    if(app.gameView.salvoes[i].player != app.viewer.id){        //oponente
                         if(app.isHit(app.gameView.salvoes[i].salvoLocations[t])){
                             document.getElementById(app.gameView.salvoes[i].salvoLocations[t]).classList.add('salvoDamage');
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
        app.drawShips();
        app.gameInformation();
        app.drawSalvoesVs();
        
    })
    .catch(function (error) {
        console.log('Looks like there was a problem: \n', error);
        alert("Shit happend Dude! You don't have acces")
    });

