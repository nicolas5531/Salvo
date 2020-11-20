var api = '/api/games';

var app = new Vue({
    el: "#app",
    data: {
        games: [],
        users: [],
        userStatistics: [],
        signUser: "",
        signPassword: "",
        player: "",
        isJoinGame: "",
    },
    methods:{
        joinGame: function(gId){
            app.gId = gId
            $.post("/api/games/"+gId+"/players").done(function(gp) { window.location.href = "/web/game.html?gp="+gp.gpId})
            .fail(function(){ $("#error-msg").html("This is not the way")})
        },
        deleteGame: function(){
            $.delete("/api/games").done(function(gp) { window.location.href = "/web/game.html?gp="+gp.gpId})
            .fail(function(){ $("#error-msg").html("This is not the way")})

        },
        newGame: function(){
            $.post("/api/games").done(function(gp) { window.location.href = "/web/game.html?gp="+gp.gpId})
            .fail(function(){ $("#error-msg").html("This is not the way")})

        },

        register: function(){
            if(app.signUser.length != 0 && app.signPassword.length > 4){
                $.post("/api/player", { email: app.signUser,password: app.signPassword})
                    .done(function() { app.logIn(app.signUser, app.signPassword)} )
                    .fail(function(error) {alert(error.responseText)})
            }
            else{
                alert("Debe llenar los datos correctamente")
            }
        },

        logOut: function(){
            $.post("/api/logout").done(function() { location.reload() })
        },

        logIn: function(){
            if(app.signUser.length != 0 && app.signPassword.length > 4){
                $.post("/api/login", { username: app.signUser,password: app.signPassword})
                    .done(function() { alert("Successful logIn!"); location.reload();})
                    .fail(function(error) {
                        if(error.status == 401)                         //cuando ejecutamos una sola accion podemos ponerlo sin llave...
                            alert("Invalid User/Password!")
                        else
                            alert("Shit happend Dude!")
                    })
            }
            else{
                alert("Debe llenar los datos correctamente(email or password)")
            }
        },

        takeNames: function(){
            var players = [];
            for(var i = 0; i < app.games.length; i++){
              for (var z = 0; z < app.games[i].gamePlayers.length ; z++){
                if (!players.includes(app.games[i].gamePlayers[z].player.email)) {
                    players.push(app.games[i].gamePlayers[z].player.email);
                }
              }
            }
            app.users = players;
        },
        takeScore: function(){
            var totScores = [];
            for(var i = 0; i < app.users.length; i++){
                totScores.push(0)
                for (var z = 0; z < app.games.length ; z++){
                    for (var j = 0; j < app.games[z].gamePlayers.length ; j++){
                        if(app.users[i] == app.games[z].gamePlayers[j].player.email){
                            if(app.games[z].gamePlayers[j].score != null){
                                totScores[i] += app.games[z].gamePlayers[j].score
                            }
                        }
                    }
                }
            }
            return(totScores);
        },
        takeWon: function(){
            var won = [];
            for(let i = 0; i < app.users.length; i++){
                won.push(0)
                for (let z = 0; z < app.games.length ; z++){
                    for (let j = 0; j < app.games[z].gamePlayers.length ; j++){
                        if(app.users[i] == app.games[z].gamePlayers[j].player.email){
                            if(app.games[z].gamePlayers[j].score == 1){
                                won[i] ++;
                            }
                        }
                    }
                }
            }
            return(won);
        },
        takeLost: function(){
            var lost = [];
            for(let i = 0; i < app.users.length; i++){
                lost.push(0)
                for (let z = 0; z < app.games.length ; z++){
                    for (let j = 0; j < app.games[z].gamePlayers.length ; j++){
                        if(app.users[i] == app.games[z].gamePlayers[j].player.email){
                            if(app.games[z].gamePlayers[j].score == 0){
                                lost[i] ++;
                            }
                        }
                    }
                }
            }
            console.log(lost);
            return(lost);
        },
        takeTied: function(){
            var tied = [];
            for(let i = 0; i < app.users.length; i++){
                tied.push(0)
                for (let z = 0; z < app.games.length ; z++){
                    for (let j = 0; j < app.games[z].gamePlayers.length ; j++){
                        if(app.users[i] == app.games[z].gamePlayers[j].player.email){
                            if(app.games[z].gamePlayers[j].score == 0.5){
                                tied[i] ++;
                            }
                        }
                    }
                }
            }
            return(tied);
        },
        statistics: function(){
            app.takeNames();
            var total = app.takeScore();
            var won = app.takeWon();
            var lost = app.takeLost();
            var tied = app.takeTied();
            for(let i = 0; i < app.users.length; i++){
                var statistics = {user: app.users[i], total: total[i], won: won[i], lost: lost[i], tied: tied[i] };
                app.userStatistics.push(statistics);
            }
        }
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
    })
    .then(function (response) {
    console.log(response)
        if (response.ok) {
            return response.json();
        }
        else{
            throw new Error(response.status)
        }
    })
    .then(function (data) {
        app.games = data.games;
        app.player = data.player;
        app.statistics();

    })
    .catch(function (error) {
        console.log('Looks like there was a problem: \n', error);
    });

