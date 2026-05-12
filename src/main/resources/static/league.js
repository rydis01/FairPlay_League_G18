const leftContent = document.getElementById("leftDynamicContent");

document.getElementById("btnCreateLeague").addEventListener("click", showCreateLeagueView);
document.getElementById("btnJoinLeague").addEventListener("click", showJoinLeagueView);
document.getElementById("btnMyLeagues").addEventListener("click", showMyLeaguesView);
document.getElementById("btnLeaderboard").addEventListener("click", showLeaderboardView);

function showCreateLeagueView() {
    leftContent.innerHTML = `
        <h3>Skapa ny liga</h3>
        <input id="leagueName" class="input-field" placeholder="Liganamn">
        <button class="action-btn" onclick="createLeague()">Skapa</button>
        <p id="result"></p>
    `;
}
function createLeague() {
    const leagueName = document.getElementById("leagueName").value;

    fetch("/api/createLeague?leagueName=" + leagueName, {
            method: "GET",
            credentials: "include"
        })
            .then(response => response.json())
            .then(success => {

                const result = document.getElementById("result");

                if (success) {
                    result.style.color = "green";
                    result.textContent = "League skapat!";

                } else {
                    result.style.color = "red";
                    result.textContent = "League kunde inte skapas!";
                }
            });
}

function showJoinLeagueView() {
    leftContent.innerHTML = `
        <h3>Gå med i liga</h3>
        <input id="inviteCode" class="input-field" placeholder="Invite-kod">
        <button class="action-btn" onclick="joinLeague()">Gå med</button>
    `;
}

function joinLeague() {
    const inviteCode = document.getElementById("inviteCode").value;

    fetch("/api/joinLeague?inviteCode=" + inviteCode, {
                method: "GET",
                credentials: "include"
            })
                .then(response => response.json())
                .then(success => {

                    const result = document.getElementById("result");

                    if (success) {
                        result.style.color = "green";
                        result.textContent = "Du blev medlem!";

                    } else {
                        result.style.color = "red";
                        result.textContent = "Du kunde inte bli medlem";
                    }
                });
}

function showMyLeaguesView() {
    leftContent.innerHTML = `
        <div class="gw-container">
            <h1>Alla ligor</h1>
            <div id="Leagues" class="matches-list"></div>
        </div>
    `;
    loadAllLeagues();
}

function loadAllLeagues() {
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

