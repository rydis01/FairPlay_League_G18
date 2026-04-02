document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("teams-container");

    // ".." betyder att vi backar ut ur web-mappen, och sedan går vi in i api-mappen.
    const jsonFilePath = "../api/allsvenskan_teams_2024.json";

    fetch(jsonFilePath)
        .then(response => {
            if (!response.ok) {
                throw new Error("Kunde inte hämta JSON-filen. Kontrollera sökvägen.");
            }
            return response.json();
        })
        .then(data => {
            const teams = data.response;

            teams.forEach(item => {
                const team = item.team;
                const venue = item.venue;

                const card = document.createElement("div");
                card.className = "card";

                card.innerHTML = `
                    <img src="${team.logo}" alt="Logga för ${team.name}">
                    <h2>${team.name}</h2>
                    <p><strong>Arena:</strong> ${venue.name}</p>
                    <p><strong>Stad:</strong> ${venue.city}</p>
                    <p><strong>Underlag:</strong> ${venue.surface === "grass" ? "Gräs" : "Konstgräs"}</p>
                `;

                container.appendChild(card);
            });
        })
        .catch(error => {
            console.error("Fel vid inläsning:", error);
            container.innerHTML = `<p class="error-message">Hoppsan! Kunde inte ladda lagen. Fel: ${error.message}</p>`;
        });
});