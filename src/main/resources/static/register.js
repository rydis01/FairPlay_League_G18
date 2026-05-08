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
