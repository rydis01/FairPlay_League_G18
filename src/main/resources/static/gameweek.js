console.log("gameweek.js LOADED");

window.onload = function () {
    document.getElementById("getRoundBtn").onclick = loadRound;
    document.getElementById("updateRoundBtn").onclick = updateRound;
    document.getElementById("submitTipsBtn").onclick = submitTips;
};

let currentMatches = [];
let tips = [];

function updateRound () {

    fetch("/api/updateGameweek", {
        method: "POST",
        credentials: "include"
    })
        .then(response => response.text())
        .then(msg => {
            console.log("Signal skickad:", msg);
        })
        .catch(err => console.error("Fel vid signal:", err));
}

function loadRound() {
    const roundId = document.getElementById("roundid").value;
    if (!roundId) {
        alert("Ange omgång först");
        return;
    }

    fetch("/api/gameweek?roundId=" + roundId, {
        method: "GET"
    })
        .then(r => r.json())
        .then(round => {
            currentMatches = round.matches || [];
            tips = [];
            renderMatches(currentMatches);
        })
        .catch(err => console.error("Kunde inte hämta gameweek:", err));
}

function renderMatches(matches) {
    const container = document.getElementById("matches");
    container.innerHTML = "";

    const now = new Date();

    const btn = document.getElementById("submitTipsBtn");
    btn.disabled = false;
    btn.textContent = "Skicka tips";
    btn.style.background = "#22c55e";
    btn.style.cursor = "pointer";

    let hasPassedMatch = false;

    matches.forEach((m, index) => {
        const card = document.createElement("div");
        card.className = "match-card league-card";

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

        if (isPassed) {
            hasPassedMatch = true;

            card.classList.add("match-passed");

            const resultDiv = document.createElement("div");
            resultDiv.className = "match-result";

            let resultText = "Resultat saknas";

            if (m.homeScore && m.awayScore) {
                resultText = `Resultat: ${m.homeScore}-${m.awayScore}`;
            } else if (m.result) {
                resultText = `Resultat: ${m.result}`;
            }

            resultDiv.textContent = resultText;
            card.appendChild(resultDiv);
        } else {
            const tipBox = document.createElement("div");
            tipBox.className = "tip-box";

            ["1", "X", "2"].forEach(choice => {
                const btn = document.createElement("div");
                btn.className = "tip-choice";
                btn.textContent = choice;
                btn.dataset.matchIndex = index;
                btn.dataset.value = choice;

                btn.onclick = () => selectTip(index, choice);

                tipBox.appendChild(btn);
            });

            card.appendChild(tipBox);
        }

        container.appendChild(card);
    });
    if (hasPassedMatch) {
        const btn = document.getElementById("submitTipsBtn");
        btn.textContent = "Kupongen är låst";
        btn.style.background = "#ef4444";
        btn.style.cursor = "not-allowed";
        btn.disabled = true;
    }
}

function selectTip(matchIndex, value) {
    tips[matchIndex] = value;

    document.querySelectorAll(`.tip-choice[data-match-index="${matchIndex}"]`)
        .forEach(btn => btn.classList.remove("active"));

    document.querySelector(
        `.tip-choice[data-match-index="${matchIndex}"][data-value="${value}"]`
    ).classList.add("active");
}

function submitTips() {
    if (tips.length !== 8 || tips.includes(undefined)) {
        alert("Du måste välja 1/X/2 för alla 8 matcher.");
        return;
    }

    const roundId = document.getElementById("roundid").value;

    const params = new URLSearchParams({
        roundId: roundId,
        tip1: tips[0],
        tip2: tips[1],
        tip3: tips[2],
        tip4: tips[3],
        tip5: tips[4],
        tip6: tips[5],
        tip7: tips[6],
        tip8: tips[7]
    });

    fetch("/api/submitTips?" + params.toString(), {
        method: "GET"
    })
        .then(r => r.text())
        .then(msg => alert(msg))
        .catch(err => console.error("Kunde inte skicka tips:", err));
}

function formatKickoff(raw) {
    if (!raw) return "Ingen tid";
    const date = new Date(raw);

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");

    const hh = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");

    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}

document.addEventListener("DOMContentLoaded", () => {
    document.body.classList.remove("fade-out");
});

document.querySelectorAll("a").forEach(link => {
    link.addEventListener("click", e => {
        const url = link.getAttribute("href");

        if (!url || url.startsWith("#")) return;

        e.preventDefault();
        document.body.classList.add("fade-out");

        setTimeout(() => {
            window.location = url;
        }, 350);
    });
});