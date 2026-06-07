/* Author Carl Rydengård and Theo Andersson */

const leftContent = document.getElementById("leftDynamicContent");

/**
 * Initierar event listeners när sidan har laddats.
 */
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("btnCreateLeague").addEventListener("click", showCreateLeagueView);
    document.getElementById("btnJoinLeague").addEventListener("click", showJoinLeagueView);
    document.getElementById("btnAllLeagues").addEventListener("click", showAllLeaguesView);
    document.getElementById("btnMyLeagues").addEventListener("click", showMyLeaguesView);
    document.getElementById("btnLeaderboard").addEventListener("click", showLeaderboardView);
});

/**
 * Byter ut innehållet i den vänstra kolumnen med en mjuk övergång (fade).
 *
 * @param {string} html HTML-strängen som ska renderas
 */
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

/**
 * Renderar vyn för att skapa en ny liga.
 */
function showCreateLeagueView() {
    fadeSwap(`
        <div class="input-box">
            <h2>Skapa liga</h2>
            <input id="leagueName" type="text" placeholder="Liganamn" aria-label="Liganamn">
            <button id="createLeagueBtn">Skapa liga</button>
            <p id="result" aria-live="polite"></p>
        </div>
    `);

    document.getElementById("createLeagueBtn").addEventListener("click", () => {
        const leagueName = document.getElementById("leagueName").value;

        if (!leagueName) {
            const result = document.getElementById("result");
            result.style.color = "#ef4444";
            result.textContent = "Ange ett liganamn.";
            return;
        }

        fetch(`/api/createLeague?leagueName=${encodeURIComponent(leagueName)}`, {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(success => {
                const result = document.getElementById("result");
                result.style.color = success ? "#22c55e" : "#ef4444"; // Matchar era CSS-variabler
                result.textContent = success ? "Ligan skapades!" : "Ligan kunde inte skapas.";
            })
            .catch(err => console.error("Kunde inte skapa liga:", err));
    });
}

/**
 * Renderar vyn för att gå med i en befintlig liga via invite-kod.
 */
function showJoinLeagueView() {
    fadeSwap(`
        <div class="input-box">
            <h2>Gå med i liga</h2>
            <input id="inviteCode" type="text" placeholder="Invite-kod" aria-label="Invite-kod">
            <button id="joinLeagueBtn">Gå med</button>
            <p id="result" aria-live="polite"></p>
        </div>
    `);

    document.getElementById("joinLeagueBtn").addEventListener("click", () => {
        const inviteCode = document.getElementById("inviteCode").value;

        if (!inviteCode) {
            const result = document.getElementById("result");
            result.style.color = "#ef4444";
            result.textContent = "Ange en invite-kod.";
            return;
        }

        fetch(`/api/joinLeague?inviteCode=${encodeURIComponent(inviteCode)}`, {
            method: "GET",
            credentials: "include"
        })
            .then(res => res.json())
            .then(success => {
                const result = document.getElementById("result");
                result.style.color = success ? "#22c55e" : "#ef4444";
                result.textContent = success ? "Du gick med i ligan!" : "Fel kod eller du är redan medlem.";
            })
            .catch(err => console.error("Kunde inte gå med i liga:", err));
    });
}

/**
 * Renderar vyn som visar användarens egna ligor.
 */
function showMyLeaguesView() {
    fadeSwap(`
        <div class="gw-container">
            <h2>Dina ligor</h2>
            <div id="Leagues" class="matches-list"></div>
        </div>
    `);

    fetch("/api/loadPlayerLeagues", { credentials: "include" })
        .then(res => res.json())
        .then(leagues => renderLeagues(leagues))
        .catch(err => console.error("Kunde inte hämta dina ligor:", err));
}

/**
 * Renderar vyn som visar alla ligor i systemet.
 */
function showAllLeaguesView() {
    fadeSwap(`
        <div class="gw-container">
            <h2>Alla ligor</h2>
            <div id="Leagues" class="matches-list"></div>
        </div>
    `);

    fetch("/api/loadAllLeagues", { credentials: "include" })
        .then(res => res.json())
        .then(leagues => renderLeagues(leagues))
        .catch(err => console.error("Kunde inte hämta alla ligor:", err));
}

/**
 * Bygger och lägger in ligakorten i gränssnittet.
 *
 * @param {Array} leagues En lista med liga-objekt
 */
function renderLeagues(leagues) {
    const container = document.getElementById("Leagues");
    container.innerHTML = "";

    if (!leagues || leagues.length === 0) {
        container.innerHTML = "<p>Inga ligor hittades.</p>";
        return;
    }

    const cards = [];

    leagues.forEach(league => {
        const card = document.createElement("div");
        card.className = "league-card fade-in";

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

/**
 * Renderar vyn för leaderboard och laddar tillgängliga ligor till menyn.
 */
function showLeaderboardView() {
    fadeSwap(`
        <div class="gw-container">
            <h2>Leaderboard</h2>

            <select id="leagueSelect" class="coupon-select" aria-label="Välj liga">
                <option value="">Välj liga...</option>
            </select>

            <div id="leaderboardContainer" class="matches-list"></div>
        </div>
    `);

    loadLeaguesForLeaderboard();
}

/**
 * Laddar användarens ligor och fyller dropdown-menyn för leaderboard.
 */
function loadLeaguesForLeaderboard() {
    fetch("/api/loadPlayerLeagues", { credentials: "include" })
        .then(res => res.json())
        .then(leagues => {
            const select = document.getElementById("leagueSelect");
            if (!select) return;

            select.innerHTML = `<option value="">Välj liga...</option>`;

            leagues.forEach(l => {
                const opt = document.createElement("option");
                opt.value = l.id;
                opt.textContent = l.name;
                select.appendChild(opt);
            });

            select.addEventListener("change", () => {
                const container = document.getElementById("leaderboardContainer");
                if (select.value) {
                    loadLeaderboard(select.value);
                } else {
                    container.innerHTML = "";
                }
            });
        })
        .catch(err => console.error("Kunde inte hämta ligor för leaderboard:", err));
}

/**
 * Hämtar och renderar topplistan för den valda ligan.
 *
 * @param {string|number} leagueId ID för ligan som ska visas
 */
function loadLeaderboard(leagueId) {
    fetch(`/api/loadLeaderboard?leagueId=${encodeURIComponent(leagueId)}`, {
        method: "GET",
        credentials: "include"
    })
        .then(res => res.json())
        .then(members => renderLeaderboard(members))
        .catch(err => console.error("Kunde inte hämta leaderboard:", err));
}

/**
 * Bygger och lägger in leaderboard-korten i gränssnittet.
 *
 * @param {Array} members En lista med medlemmar sorterade efter poäng
 */
function renderLeaderboard(members) {
    const container = document.getElementById("leaderboardContainer");
    container.innerHTML = "";

    if (!members || members.length === 0) {
        container.innerHTML = "<p>Inga medlemmar i denna liga ännu.</p>";
        return;
    }

    const cards = [];

    members.forEach((member, index) => {
        const card = document.createElement("div");
        card.className = "league-card fade-in";

        card.innerHTML = `
            <div class="match-teams">${index + 1}. ${member.username}</div>
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