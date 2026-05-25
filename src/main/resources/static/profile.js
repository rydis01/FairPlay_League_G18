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
            console.log(coupons);

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

    console.log("🔵 Hämtar kupong med ID:", couponId);

    fetch("/api/getCoupon?couponId=" + couponId, {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(coupon => {
            console.log("🟢 Backend svar (coupon):", coupon);
                        console.log("🟢 Tips från backend:", coupon.tips);
            renderCouponMatches(coupon.tips);
        });
}

function renderCouponMatches(tips) {
    console.log("🟡 renderCouponMatches() fick tips:", tips);

    const container = document.getElementById("couponDetails");
    container.innerHTML = "";

    if (Array.isArray(tips)) {
        console.log("🟣 Tips är en ARRAY med längd:", tips.length);

        tips.forEach((tip, index) => {
            console.log(`🔍 Tip #${index}:`, tip);
        });
    }

    // Om tips är objekt/map:
        if (typeof tips === "object" && !Array.isArray(tips)) {
            console.log("🟣 Tips är ett OBJEKT med keys:", Object.keys(tips));

            Object.entries(tips).forEach(([matchId, choice]) => {
                console.log(`🔍 Tip för match ${matchId}:`, choice);
            });
        }
    const cards = [];

    // tips är en ARRAY av objekt
    console.log("tips från backend:", tips);

    tips.forEach((tip, index) => {
        console.log("enskilt tip:", tip);

        const card = document.createElement("div");
        card.className = "match-card fade-in";

        const teamsDiv = document.createElement("div");
        teamsDiv.className = "match-teams";
        // om backend skickar tip.match, använd det, annars visa index
        teamsDiv.textContent = tip.match ?? ("Match-ID: " + index);
        card.appendChild(teamsDiv);

        const yourTip = document.createElement("div");
        yourTip.className = "match-time";

        // choice kan vara sträng eller objekt → hantera båda
        let choice = tip.choice;
        if (typeof choice === "object" && choice !== null) {
            // gissa på vanliga fält, annars stringify
            choice = choice.value ?? choice.guess ?? JSON.stringify(choice);
        }

        yourTip.textContent = "Ditt tips: " + (choice ?? "-");
        card.appendChild(yourTip);

        const resultDiv = document.createElement("div");
        resultDiv.className = "match-result";

        let correct = tip.correctResult;
        if (typeof correct === "object" && correct !== null) {
            correct = correct.value ?? correct.result ?? JSON.stringify(correct);
        }

        resultDiv.textContent = "Rätt resultat: " + (correct ?? "Ej klart");
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
