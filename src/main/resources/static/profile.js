window.onload = function () {
    fetch("/api/userinfo", {
        credentials: "include"
    })
        .then(response => response.json())
        .then(user => {
            document.getElementById("username").textContent = user.username;
            document.getElementById("email").textContent = user.email;
            document.getElementById("role").textContent = user.role;
            document.getElementById("createdAt").textContent = formatDate(user.createdAt);
        });
};

function formatDate(raw) {
    const date = new Date(raw);

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");

    const hh = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");

    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}

function logout() {
    window.location.href = "/login.html";
}
