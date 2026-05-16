window.onload = function () {
    loadCoupons();
    loadUserinfo();
};

function loadCoupons() {

    fetch("/api/getCoupons", {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(coupons => {

            const select = document.getElementById("couponSelect");
            select.innerHTML = "";

            coupons.forEach(c => {
                const option = document.createElement("option");
                option.value = c.id;
                option.textContent = "Omgång " + c.roundId;
                select.appendChild(option);
            });

            if (coupons.length > 0) {
                loadCouponDetails(coupons[0].id);
            }
        });
}

document.getElementById("couponSelect").onchange = function () {
    loadCouponDetails(this.value);
};

function loadCouponDetails(couponId) {

    fetch("/api/getCoupon?couponId=" + couponId, {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(coupon => {
            renderCouponMatches(coupon.tips);
        });
}

function renderCouponMatches(tips) {
    const container = document.getElementById("couponDetails");
    container.innerHTML = "";

    const cards = [];

    tips.forEach(tip => {

        const card = document.createElement("div");
        card.className = "match-card fade-in";

        const teamsDiv = document.createElement("div");
        teamsDiv.className = "match-teams";
        teamsDiv.textContent = tip.match;
        card.appendChild(teamsDiv);

        const yourTip = document.createElement("div");
        yourTip.className = "match-time";
        yourTip.textContent = "Ditt tips: " + tip.choice;
        card.appendChild(yourTip);

        const resultDiv = document.createElement("div");
        resultDiv.className = "match-result";
        resultDiv.textContent = "Rätt resultat: " + (tip.correctResult ?? "Ej klart");
        card.appendChild(resultDiv);

        container.appendChild(card);
        cards.push(card);
    });

    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            cards.forEach(card => card.classList.add("show"));
        });
    });
}


function loadUserinfo() {
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
}

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
    fetch("/api/logout", { method: "GET", credentials: "include" })
        .then(() => {
            window.location.href = "/login.html";
        });
}

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
