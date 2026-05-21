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
document.getElementById("btnAllLeagues").addEventListener("click", showAllLeaguesView);
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

    fetch("/api/loadPlayerLeagues", { credentials: "include" })
        .then(r => r.json())
        .then(leagues => renderLeagues(leagues));
}

function showAllLeaguesView() {
    fadeSwap(`
        <div class="gw-container">
            <h2>Alla ligor</h2>
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
            <div class="match-time">ID: ${league.id}</div>
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
        <div class="gw-container">
            <h2>Leaderboard</h2>

            <select id="leagueSelect" class="coupon-select">
                <option value="">Välj liga...</option>
            </select>

            <div id="leaderboardContainer" class="matches-list"></div>
        </div>
    `);

    setTimeout(() => loadLeaguesForLeaderboard(), 50);
}


function loadLeaguesForLeaderboard() {
    fetch("/api/loadPlayerLeagues", { credentials: "include" })
        .then(r => r.json())
        .then(leagues => {

            const select = document.getElementById("leagueSelect");
            select.innerHTML = `<option value="">Välj liga...</option>`;

            leagues.forEach(l => {
                const opt = document.createElement("option");
                opt.value = l.id;
                opt.textContent = l.name;
                select.appendChild(opt);
            });

            select.onchange = () => {
                if (select.value) loadLeaderboard(select.value);
            };
        });
}

function loadLeaderboard(leagueId) {
    fetch("/api/leaderboard?leagueId=" + leagueId, {
        method: "GET",
        credentials: "include"
    })
        .then(r => r.json())
        .then(members => renderLeaderboard(members));
}

function renderLeaderboard(members) {
    const container = document.getElementById("leaderboardContainer");
    container.innerHTML = "";

    const cards = [];

    members.forEach(member => {
        const card = document.createElement("div");
        card.className = "match-card fade-in";

        card.innerHTML = `
            <div class="match-teams">${member.username}</div>
            <div class="match-time">ID: ${member.userId}</div>
            <div class="match-time">${member.totalScore} poäng</div>
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


