console.log("gameweek.js LOADED");

let currentMatchIds = []; // Sparar ID:n

document.addEventListener("DOMContentLoaded", function () {
    const fetchBtn = document.getElementById("fetchRoundBtn");
    const submitBtn = document.getElementById("submitCouponBtn");

    // Lyssna på klick för att hämta omgång
    fetchBtn.addEventListener("click", fetchRound);

    // Lyssna på klick för inlämning
    submitBtn.addEventListener("click", submitCoupon);
});

function fetchRound() {
    const roundId = document.getElementById("roundInput").value;
    const container = document.getElementById("matches");
    const statusLabel = document.getElementById("statusMessage");
    const submitBtn = document.getElementById("submitCouponBtn");

    statusLabel.textContent = "Hämtar...";
    statusLabel.className = "status-message";

    // Anropa er Spring Boot Backend
    // vi måste bygga en GET-endpoint i Spring Boot som heter t.ex. /api/gameweek/{roundId}
    fetch(`/api/gameweek/${roundId}`)
        .then(response => {
            if (!response.ok) throw new Error("Kunde inte hämta omgången");
            return response.json();
        })
        .then(data => {
            container.innerHTML = "";
            currentMatchIds = []; // Rensa gamla ID:n

            const isRoundOpen = data.status === "Open";

            if (!isRoundOpen) {
                statusLabel.textContent = `Omgång ${roundId} är stängd för inlämning!`;
                statusLabel.className = "status-message error";
                submitBtn.disabled = true;
            } else {
                statusLabel.textContent = "";
                submitBtn.disabled = false;
            }

            // Loopa igenom matcherna
            data.matches.forEach((m, index) => {
                currentMatchIds.push(m.id); // Spara match ID

                const card = document.createElement("div");
                card.className = "match-card";

                // Avgör om radioknapparna ska vara klickbara
                const disabledState = isRoundOpen ? "" : "disabled";

                card.innerHTML = `
                    <div class="match-time">${formatKickoff(m.kickOff)}</div>
                    <div class="match-teams">${index + 1}. ${m.homeTeam} vs ${m.awayTeam}</div>

                    <div class="bet-options">
                        <label class="bet-option">
                            <span>1</span>
                            <input type="radio" name="match_${m.id}" value="1" ${disabledState}>
                        </label>
                        <label class="bet-option">
                            <span>X</span>
                            <input type="radio" name="match_${m.id}" value="X" ${disabledState}>
                        </label>
                        <label class="bet-option">
                            <span>2</span>
                            <input type="radio" name="match_${m.id}" value="2" ${disabledState}>
                        </label>
                    </div>
                `;

                container.appendChild(card);
            });

            if (data.matches.length === 0) {
                statusLabel.textContent = "Inga matcher hittades.";
                submitBtn.disabled = true;
            }

        })
        .catch(err => {
            console.error(err);
            statusLabel.textContent = "Fel vid hämtning av data.";
            statusLabel.className = "status-message error";
        });
}

function submitCoupon() {
    const roundId = document.getElementById("roundInput").value;
    const statusLabel = document.getElementById("statusMessage");
    const tips = {};

    // Gå igenom alla sparade match-ID:n och hitta vilken radioknapp som är ikryssad
    for (let matchId of currentMatchIds) {
        const selectedRadio = document.querySelector(`input[name="match_${matchId}"]:checked`);
        if (selectedRadio) {
            tips[matchId] = selectedRadio.value;
        }
    }

    // Validering
    if (Object.keys(tips).length !== 8) {
        statusLabel.textContent = "Du måste tippa alla 8 matcher innan du lämnar in!";
        statusLabel.className = "status-message error";
        return;
    }

    // Skicka datan till Spring Boot
    // vi måste bygga en POST-endpoint i Spring Boot på /api/gameweek/submit
    fetch(`/api/gameweek/submit`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            roundId: parseInt(roundId),
            userId: 1, // Ändra till inloggad användare senare
            tips: tips
        })
    })
    .then(response => {
        if (!response.ok) throw new Error("Inlämning misslyckades");
        statusLabel.textContent = "Kupong inlämnad! Lycka till.";
        statusLabel.className = "status-message success";
        document.getElementById("submitCouponBtn").disabled = true;
    })
    .catch(err => {
        console.error(err);
        statusLabel.textContent = "Något gick fel vid inlämning.";
        statusLabel.className = "status-message error";
    });
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