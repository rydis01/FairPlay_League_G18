/**
 * Konstanter för att undvika "magiska nummer".
 */
const REQUIRED_TIPS_COUNT = 8;

let currentMatches = [];
let tips = [];

/**
 * Initierar sidan när DOM:en har laddats helt.
 */
document.addEventListener("DOMContentLoaded", () => {
    document.body.classList.remove("fade-out");
    document.getElementById("getRoundBtn").addEventListener("click", loadRound);
    document.getElementById("updateRoundBtn").addEventListener("click", updateRound);
    document.getElementById("submitTipsBtn").addEventListener("click", submitTips);

    loadLeagues();

    setupPageTransitions();
});

/**
 * Sätter upp mjuka övergångar (fade) när användaren klickar på länkar.
 */
function setupPageTransitions() {
    document.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", e => {
            const url = link.getAttribute("href");

            // Ignorera tomma länkar eller ankarlänkar
            if (!url || url.startsWith("#")) return;

            e.preventDefault();
            document.body.classList.add("fade-out");

            setTimeout(() => {
                window.location = url;
            }, 350);
        });
    });
}

/**
 * Skickar en signal till servern att uppdatera matchdata från externa API:er.
 */
function updateRound() {
    fetch("/api/updateGameweek", {
        method: "POST",
        credentials: "include"
    })
        .then(res => res.text())
        .then(msg => console.log("Signal skickad:", msg))
        .catch(err => console.error("Fel vid signal:", err));
}

/**
 * Hämtar en specifik omgång från servern och renderar dess matcher.
 */
function loadRound() {
    const roundId = document.getElementById("roundId").value;
    
    if (!roundId) {
        alert("Ange omgång först");
        return;
    }

    fetch(`/api/gameweek?roundId=${roundId}`)
        .then(res => res.json())
        .then(round => {
            currentMatches = round.matches || [];
            tips = [];
            renderMatches(currentMatches);
        })
        .catch(err => console.error("Kunde inte hämta gameweek:", err));
}

/**
 * Renderar matchkorten i gränssnittet baserat på hämtad data.
 *
 * @param {Array} matches Lista med matchobjekt från servern
 */
function renderMatches(matches) {
    const container = document.getElementById("matches");
    container.innerHTML = "";

    const now = new Date();
    const submitBtn = document.getElementById("submitTipsBtn");

    submitBtn.disabled = false;
    submitBtn.textContent = "Skicka tips";
    submitBtn.classList.remove("btn-locked");

    let hasPassedMatch = false;

    matches.forEach((m, index) => {
        const card = document.createElement("div");
        card.className = "match-card";

        const timeDiv = document.createElement("div");
        timeDiv.className = "match-time";
        timeDiv.textContent = formatKickoff(m.kickOff);
        card.appendChild(timeDiv);

        const teamsDiv = document.createElement("div");
        teamsDiv.className = "match-teams";
        teamsDiv.textContent = `${m.homeTeam} vs ${m.awayTeam}`;
        card.appendChild(teamsDiv);

        const kickoffDate = new Date(m.kickOff);
        const isPassed = kickoffDate < now;

        // Hantering av passerade matcher
        if (isPassed) {
            hasPassedMatch = true;
            card.classList.add("match-passed");

            const resultDiv = document.createElement("div");
            resultDiv.className = "match-result";

            let resultText = "Resultat saknas";

            // En säkerhetskontroll så att 0-0 inte tolkas som 'false'
            if (m.homeScore !== undefined && m.awayScore !== undefined) {
                resultText = `Resultat: ${m.homeScore}-${m.awayScore}`;
            } else if (m.result) {
                resultText = `Resultat: ${m.result}`;
            }

            resultDiv.textContent = resultText;
            card.appendChild(resultDiv);
            
        // Hantering av öppna matcher
        } else {
            const tipBox = document.createElement("div");
            tipBox.className = "tip-box";

            ["1", "X", "2"].forEach(choice => {
                const btn = document.createElement("div");
                btn.className = "tip-choice";
                btn.textContent = choice;
                btn.dataset.matchIndex = index;
                btn.dataset.value = choice;

                btn.addEventListener("click", () => selectTip(index, choice));

                tipBox.appendChild(btn);
            });

            card.appendChild(tipBox);
        }

        container.appendChild(card);
    });

    // Om någon match i omgången har startat, spärra inlämningen
    if (hasPassedMatch) {
        submitBtn.textContent = "Kupongen är låst";
        submitBtn.classList.add("btn-locked");
        submitBtn.disabled = true;
    }
}

/**
 * Hanterar användarens val av tipstecken för en specifik match.
 *
 * @param {number} matchIndex Matchens indexering i tips-arrayen
 * @param {string} value Det valda tecknet ("1", "X" eller "2")
 */
function selectTip(matchIndex, value) {
    tips[matchIndex] = value;

    document.querySelectorAll(`.tip-choice[data-match-index="${matchIndex}"]`)
        .forEach(btn => btn.classList.remove("active"));

    document.querySelector(`.tip-choice[data-match-index="${matchIndex}"][data-value="${value}"]`)
        .classList.add("active");
}

/**
 * Validerar och skickar in kupongen till servern.
 */
function submitTips() {
    if (tips.length !== REQUIRED_TIPS_COUNT || tips.includes(undefined)) {
        alert(`Du måste välja 1/X/2 för alla ${REQUIRED_TIPS_COUNT} matcher.`);
        return;
    }

    const roundId = document.getElementById("roundId").value;
    const leagueSelect = document.getElementById("leagueSelect");
    const leagueId = leagueSelect ? leagueSelect.value : null;

    if (!leagueId) {
        alert("Du måste välja en liga innan du kan skicka kupongen.");
        return;
    }

    const params = new URLSearchParams({
        roundId: roundId,
        leagueId: leagueId,
        tip1: tips[0],
        tip2: tips[1],
        tip3: tips[2],
        tip4: tips[3],
        tip5: tips[4],
        tip6: tips[5],
        tip7: tips[6],
        tip8: tips[7]
    });

    fetch(`/api/submitTips?${params.toString()}`)
        .then(res => res.text())
        .then(msg => alert(msg))
        .catch(err => console.error("Kunde inte skicka tips:", err));
}

/**
 * Formaterar en rå datumsträng (från API/Databas) till YYYY-MM-DD HH:mm.
 *
 * @param {string} raw Den oformaterade tidssträngen
 * @returns {string} Den formaterade tiden
 */
function formatKickoff(raw) {
    if (!raw) return "Ingen tid";
    
    const date = new Date(raw);

    if (isNaN(date.getTime())) return "Ogiltig tid";

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");

    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}

/**
 * Hämtar och renderar listan över ligor som inloggad användare tillhör.
 */
function loadLeagues() {
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
        })
        .catch(err => console.error("Kunde inte hämta ligor:", err));
}