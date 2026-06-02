/**
 * Initierar event listeners när sidan har laddats.
 */
document.addEventListener("DOMContentLoaded", () => {
    // Ta bort fade-out för att visa sidan mjukt
    document.body.classList.remove("fade-out");

    // Koppla knappar till funktioner med moderna EventListeners
    document.getElementById("registerBtn").addEventListener("click", handleRegistration);
    document.getElementById("togglePassword").addEventListener("click", togglePasswordVisibility);

    // Sätt upp sidövergångar för navigeringslänkar
    setupPageTransitions();
});

/**
 * Hanterar registreringen mot servern.
 */
function handleRegistration() {
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const pass = document.getElementById("password").value;

    fetch("/api/register?username=" + username + "&email=" + email + "&password=" + pass, {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(success => {
            const result = document.getElementById("result");

            if (success) {
                result.style.color = "green";
                result.textContent = "Konto skapat!";

                window.location.href = "login.html";
            } else {
                result.style.color = "red";
                result.textContent = "Email används redan.";
            }
        });
}

/**
 * Växlar synligheten på lösenordsfältet (mellan text och password).
 */
function togglePasswordVisibility() {
    const passField = document.getElementById("password");

    if (passField.type === "password") {
        passField.type = "text";
        this.src = "fairplayleague.png";
    } else {
        passField.type = "password";
        this.src = "fairplayleague.png";
    }
}

/**
 * Sätter upp mjuka övergångar (fade) när användaren klickar på standardlänkar.
 */
function setupPageTransitions() {
    document.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", e => {
            const url = link.getAttribute("href");

            // Ignorera tomma länkar eller ankarlänkar (som href="#")
            if (!url || url.startsWith("#")) return;

            e.preventDefault();
            document.body.classList.add("fade-out");

            setTimeout(() => {
                window.location = url;
            }, 350);
        });
    });
}