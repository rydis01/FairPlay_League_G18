window.onload = function () {

    fetch("/api/gameweek")
        .then(response => response.json())
        .then(round => {

            const container = document.getElementById("matches");
            container.innerHTML = "";

            round.matches.forEach(m => {
                const card = document.createElement("div");
                card.className = "match-card";

                card.innerHTML = `
                    <div class="match-time">${formatKickoff(m.kickOff)}</div>
                    <div class="match-teams">${m.homeTeam} vs ${m.awayTeam}</div>
                `;

                container.appendChild(card);
            });
        });
};

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
