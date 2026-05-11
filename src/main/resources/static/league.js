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
                        result.textContent = "League skapat!";

                    } else {
                        result.style.color = "red";
                        result.textContent = "League kunde inte skapas!";
                    }
                });
}

function showMyLeaguesView() {
    leftContent.innerHTML = `
        <h3>Dina ligor</h3>
        <div id="myLeaguesList"></div>
    `;
    loadUserLeagues();
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

