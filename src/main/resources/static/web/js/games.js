var api = '/api/games';


var app = new Vue({
    el: "#app",
    data: {
        games: [],
        users: [],
        userStatistics: [],

    },
    methods:{
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
            console.log(app.users);
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
            console.log(totScores);
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
            console.log(won);
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
            console.log(tied);
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
        app.games = data;
        app.takeNames();
        app.takeScore();
        app.takeWon();
        app.takeLost();
        app.takeTied();
        app.statistics();
    })
    .catch(function (error) {
        console.log('Looks like there was a problem: \n', error);
    });
