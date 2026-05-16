document.getElementById("loginBtn").onclick = function () {
    const email = document.getElementById("email").value;
    const pass = document.getElementById("password").value;

    fetch("/api/login?email=" + email + "&password=" + pass, {
    method: "GET",
    credentials: "include"
    })
        .then(response => response.json())
        .then(success => {

            const result = document.getElementById("result");

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
};

document.getElementById("togglePassword").onclick = function () {
    const passField = document.getElementById("password");

    if (passField.type === "password") {
        passField.type = "text";
        this.src = "fairplayleague.png";
    } else {
        passField.type = "password";
        this.src = "fairplayleague.png";
    }
};


document.getElementById("registerBtn").onclick = function () {
    window.location.href = "registerUser.html";
};

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