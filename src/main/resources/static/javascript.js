 document.getElementById("loginBtn").onclick = function () {

     const email = document.getElementById("email").value;
     const pass = document.getElementById("password").value;

     fetch("/api/login?email=" + email + "&password=" + pass, {
         method: "POST"
     })
         .then(response => response.json())
         .then(success => {

             const result = document.getElementById("result");

             if (success) {
                 result.style.color = "green";
                 result.textContent = "Inloggning lyckades!";

                 window.location.href = "kupong.html";

             } else {
                 result.style.color = "red";
                 result.textContent = "Fel e-post eller lösenord. Försök igen.";

                 // Rensa lösenordet så man kan skriva nytt
                 document.getElementById("password").value = "";
             }
         });
 };

