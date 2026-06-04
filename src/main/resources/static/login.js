/* Author Carl Rydengård */
/**
 * Initierar event listeners när sidan har laddats.
 */
document.addEventListener("DOMContentLoaded", () => {
    document.body.classList.remove("fade-out");

    // Koppla knappar till funktioner med EventListeners
    document.getElementById("loginBtn").addEventListener("click", handleLogin);
    document.getElementById("togglePassword").addEventListener("click", togglePasswordVisibility);
    document.getElementById("registerBtn").addEventListener("click", navigateToRegister);
    
    // Sätt upp sidövergångar för navigeringslänkar
    setupPageTransitions();
});

/**
 * Hanterar inloggningsförsöket mot servern.
 */
function handleLogin() {
    const email = document.getElementById("email").value;
    const pass = document.getElementById("password").value;
    const result = document.getElementById("result");

    fetch("/api/login?email=" + email + "&password=" + pass, {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(success => {
            if (success) {
                result.style.color = "green";
                result.textContent = "Inloggning lyckades!";
                
                window.location.href = "gameweek.html";
            } else {
                result.style.color = "red";
                result.textContent = "Fel e-post eller lösenord. Försök igen.";
                
                document.getElementById("password").value = "";
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
 * Navigerar användaren till registreringssidan med en mjuk övergång.
 */
function navigateToRegister() {
    document.body.classList.add("fade-out");
    setTimeout(() => {
        window.location.href = "registerUser.html";
    }, 350);
}

/**
 * Sätter upp mjuka övergångar (fade) när användaren klickar på standardlänkar.
 */
function setupPageTransitions() {
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
}