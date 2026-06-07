/* Author Carl Rydengård and Theo Andersson */
/**
 * Initierar event listeners och laddar in data när sidan har laddats.
 */
document.addEventListener("DOMContentLoaded", () => {

    document.body.classList.remove("fade-out");

    loadCoupons();
    loadUserinfo();

    const couponSelect = document.getElementById("couponSelect");
    if (couponSelect) {
        couponSelect.addEventListener("change", function () {
            loadCouponDetails(this.value);
        });
    }

    setupPageTransitions();
});

/**
 * Hämtar användarens inlämnade kuponger och fyller dropdown-menyn.
 */
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
        })
        .catch(err => console.error("Kunde inte hämta kuponger:", err));
}

/**
 * Hämtar detaljerad information (tips och resultat) för en specifik kupong.
 */
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
        })
        .catch(err => console.error("Kunde inte hämta kupongdetaljer:", err));
}

/**
 * Renderar matchkorten för den valda kupongen i gränssnittet med fade-in-effekt.
 */
function renderCouponMatches(tips) {
    const container = document.getElementById("couponDetails");
    container.innerHTML = "";
    const cards = [];

    if (!tips || tips.length === 0) {
        container.innerHTML = "<p>Inga tips hittades på denna kupong.</p>";
        return;
    }

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

    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            cards.forEach(card => card.classList.add("show"));
        });
    });
}

/**
 * Hämtar och visar den inloggade användarens profilinformation.
 */
function loadUserinfo() {
    fetch("/api/userinfo", {
        credentials: "include"
    })
        .then(response => response.json())
        .then(user => {
            if(user) {
                document.getElementById("username").textContent = user.username;
                document.getElementById("email").textContent = user.email;
                document.getElementById("role").textContent = user.role;
                document.getElementById("createdAt").textContent = formatDate(user.createdAt);
            }
        })
        .catch(err => console.error("Kunde inte ladda användarinfo (Kanske inte inloggad?):", err));
}

/**
 * Formaterar en rå datumsträng (från databasen) till YYYY-MM-DD HH:mm.
 *
 * @param {string} raw Den oformaterade tidssträngen
 * @returns {string} Den formaterade tiden
 */
function formatDate(raw) {
    if (!raw) return "";

    const date = new Date(raw);
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, "0");
    const dd = String(date.getDate()).padStart(2, "0");
    const hh = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");

    return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
}

/**
 * Loggar ut användaren och omdirigerar tillbaka till inloggningssidan.
 */
function logout() {
    fetch("/api/logout", {
        method: "GET",
        credentials: "include"
    })
        .then(() => {
            window.location.href = "login.html";
        })
        .catch(err => console.error("Fel vid utloggning:", err));
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