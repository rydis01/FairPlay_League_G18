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

    tips.forEach((tip, index) => {
        const card = document.createElement("div");
        card.className = "match-card fade-in";

        const teamsDiv = document.createElement("div");
        teamsDiv.className = "match-teams";
        teamsDiv.textContent = tip.match ?? ("Match-ID: " + index);
        card.appendChild(teamsDiv);

        const yourTip = document.createElement("div");
        yourTip.className = "match-time";

        let choice = tip.choice;
        if (typeof choice === "object" && choice !== null) {
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

    requestAnimationFrame(()