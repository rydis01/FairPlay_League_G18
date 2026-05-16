const leftContent = document.getElementById("leftDynamicContent");

function fadeSwap(html) {
    leftContent.innerHTML = html;

    const el = leftContent.firstElementChild;
    if (!el) return;

    el.classList.add("fade-in");

    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            el.classList.add("show");
        });
    });
}

document.getElementById("btnCreateLeague").addEventListener("click", showCreateLeagueView);
document.getElementById("btnJoinLeague").addEventListener("click", showJoinLeagueView);
document.getElementById("btnMyLeagues").addEventListener("click", showMyLeaguesView);
document.getElementById("btnLeaderboard").addEventListener("click", showLeaderboardView);

function showCreateLeagueView() {
    fadeSwap(`
        <div class="input-box">
            <h2>Skapa liga</h2>
            <input id="leagueName" type="text" placeholder="Liganamn">
            <button id="createLeagueBtn">Skapa liga</button>
            <p id="result"></p>
        </div>
    `);

    setTimeout(() => {
        document.getElementById("createLeagueBtn").onclick = () => {
            const leagueName = document.getElementById("leagueName").value;

            fetch("/api/createLeague?leagueName=" + leagueName, {
                method: "GET",
                credentials: "include"
            })
                .then(r => r.json())
                .then(success => {
                    const result = document.getElementById("result");
                    result.style.color = success ? "green" : "red";
                    result.textContent = success ? "Ligan skapades!" : "Ligan kunde inte skapas.";
                });
        };
    }, 50);
}

function showJoinLeagueView() {
    fadeSwap(`
        <div class="input-box">
            <h2>Gå med i liga</h2>
            <input id="inviteCode" type="text" placeholder="Invite-kod">
            <button id="joinLeagueBtn">Gå med</button>
            <p id="result"></p>
        </div>
    `);

    setTimeout(() => {
        document.getElementById("joinLeagueBtn").onclick = () => {
            const inviteCode = document.getElementById("inviteCode").value;

            fetch("/api/joinLeague?inviteCode=" + inviteCode, {
                method: "GET",
                credentials: "include"
            })
                .then(r => r.json())
                .then(success => {
                    const result = document.getElementById("result");
                    result.style.color = success ? "green" : "red";
                    result.textContent = success ? "Du gick med i ligan!" : "Fel kod eller du är redan medlem.";
                });
        };
    }, 50);
}

function showMyLeaguesView() {
    fadeSwap(`
        <div class="gw-container">
            <h2>Dina ligor</h2>
            <div id="Leagues" class="matches-list"></div>
        </div>
    `);

    fetch("/api/loadAllLeagues", { credentials: "include" })
        .then(r => r.json())
        .then(leagues => renderLeagues(leagues));
}

function renderLeagues(leagues) {
    const container = document.getElementById("Leagues");
    container.innerHTML = "";

    const cards = [];

    leagues.forEach(league => {
        const card = document.createElement("div");
        card.className = "match-card fade-in";

        card.innerHTML = `
            <div class="match-teams">${league.name}</div>
            <div class="match-time">Invite-kod: ${league.inviteCode}</div>
        `;

        container.appendChild(card);
        cards.push(card);
    });

    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            cards.forEach(card => card.classList.add("show"));
        });
    });
}

function showLeaderboardView() {
    fadeSwap(`
        <div class="input-box">
            <h2>Leaderboard</h2>
            <p>Välj en liga först.</p>
        </div>
    `);
}
