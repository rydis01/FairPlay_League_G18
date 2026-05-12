const leftContent = document.getElementById("leftDynamicContent");

document.getElementById("btnCreateLeague").addEventListener("click", showCreateLeagueView);
document.getElementById("btnJoinLeague").addEventListener("click", showJoinLeagueView);
document.getElementById("btnMyLeagues").addEventListener("click", showMyLeaguesView);
document.getElementById("btnLeaderboard").addEventListener("click", showLeaderboardView);

function showCreateLeagueView() {
    leftContent.innerHTML = `
        <div class="input-box">
            <h2>Skapa liga</h2>

            <input id="leagueName" type="text" placeholder="Liganamn">

            <button id="createLeagueBtn">Skapa liga</button>

            <p id="result"></p>
        </div>
    `;

    document.getElementById("createLeagueBtn").onclick = function () {
        const leagueName = document.getElementById("leagueName").value;

        fetch("/api/createLeague?leagueName=" + leagueName, {
            method: "GET",
            credentials: "include"
        })
            .then(r => r.json())
            .then(success => {
                const result = document.getElementById("result");

                if (success) {
                    result.style.color = "green";
                    result.textContent = "Ligan skapades!";
                } else {
                    result.style.color = "red";
                    result.textContent = "Ligan kunde inte skapas.";
                }
            });
    };
}

function showJoinLeagueView() {
    leftContent.innerHTML = `
        <div class="input-box">
            <h2>Gå med i liga</h2>

            <input id="inviteCode" type="text" placeholder="Invite-kod">

            <button id="joinLeagueBtn">Gå med</button>

            <p id="result"></p>
        </div>
    `;

    document.getElementById("joinLeagueBtn").onclick = function () {
        const inviteCode = document.getElementById("inviteCode").value;

        fetch("/api/joinLeague?inviteCode=" + inviteCode, {
            method: "GET",
            credentials: "include"
        })
            .then(r => r.json())
            .then(success => {
                const result = document.getElementById("result");

                if (success) {
                    result.style.color = "green";
                    result.textContent = "Du gick med i ligan!";
                } else {
                    result.style.color = "red";
                    result.textContent = "Fel kod eller du är redan medlem.";
                }
            });
    };
}


function showMyLeaguesView() {
    leftContent.innerHTML = `
        <div class="gw-container">
            <div id="Leagues" class="matches-list"></div>
        </div>
    `;

    fetch("/api/loadAllLeagues", {
    credentials: "include"
    })
        .then(r => r.json())
        .then(leagues => {
            renderLeagues(leagues);
        })
        .catch(err => console.error("Kunde inte hämta ligor:", err));

}

function renderLeagues(leagues) {
    const container = document.getElementById("Leagues");
    container.innerHTML = "";

    leagues.forEach(league => {
        const card = document.createElement("div");
        card.className = "match-card league-card";

        const nameDiv = document.createElement("div");
        nameDiv.className = "match-teams";
        nameDiv.textContent = league.name;
        card.appendChild(nameDiv);

        const inviteDiv = document.createElement("div");
        inviteDiv.className = "match-time";
        inviteDiv.textContent = "Invite-kod: " + league.inviteCode;
        card.appendChild(inviteDiv);

        container.appendChild(card);
    });
}



function showLeaderboardView() {
    const leagueId = document.getElementById("leagueSelect").value;

    if (!leagueId) {
        leftContent.innerHTML = "<p>Välj en liga först.</p>";
        return;
    }

    leftContent.innerHTML = `
        <h3>Poängbräda</h3>
        <div id="leaderboard"></div>
    `;

    loadLeaderboard(leagueId);
}

