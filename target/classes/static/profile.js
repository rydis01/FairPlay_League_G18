window.onload = function () {
    fetch("/api/userinfo")
        .then(response => response.text())
        .then(data => {
            document.getElementById("username").textContent = extract(data, "username='", "'");
            document.getElementById("email").textContent = extract(data, "email='", "'");
            document.getElementById("role").textContent = extract(data, "role=", ",");
            document.getElementById("createdAt").textContent = formatDate(extract(data, "createdAt=", "}"));

        });
};

function extract(text, start, end) {
    let s = text.indexOf(start);
    if (s === -1) return "";
    s += start.length;
    let e = text.indexOf(end, s);
    return text.substring(s, e);
}

function formatDate(raw) {
    const date = new Date(raw);

    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");

    const hh = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");

    return `Tid: ${yyyy}-${mm}-${dd}, ${hh}:${min}`;
}


function logout() {
    window.location.href = "/login.html";
}

