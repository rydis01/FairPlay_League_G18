document.getElementById("registerBtn").onclick = function () {
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
