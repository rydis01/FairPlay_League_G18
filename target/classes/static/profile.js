/**
 * Initierar event listeners och laddar in data när sidan har laddats.
 */
document.addEventListener("DOMContentLoaded", () => {
    // Ta bort fade-out för att visa sidan mjukt
    document.body.classList.remove("fade-out");

    // Ladda in användarens data
    loadCoupons();
    loadUserinfo();
<<<<<<< Updated upstream
};
=======

    // Koppla event listener till dropdown-menyn för kuponger
    const couponSelect = document.getElementById("couponSelect");
    if (couponSelect) {
        couponSelect.addEventListener("change", function () {
            loadCouponDetails(this.value);
        });
    }
>>>>>>> Stashed changes

    // Sätt upp sidövergångar för navigeringslänkar
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

            // Ladda detaljerna för den första kupongen automatiskt om det finns någon
            if (coupons.length > 0) {
                loadCouponDetails(coupons[0].id);
            }
        });
}

<<<<<<< Updated upstream
document.getElementById("couponSelect").onchange = function () {
    loadCouponDetails(this.value);
};

=======
/**
 * Hämtar detaljerad information (tips och resultat) för en specifik kupong.
 *
 * @param {string|number} couponId ID för kupongen som ska hämtas
 */
>>>>>>> Stashed changes
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

/**
 * Renderar matchkorten för den valda kupongen i gränssnittet med fade-in-effekt.
 *
 * @param {Array} tips En lista med tipsobjekt för kupongen
 */
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
<<<<<<< Updated upstream

        // choice kan vara sträng eller objekt → hantera båda
=======
        
>>>>>>> Stashed changes
        let choice = tip.choice;
        // Hantera eventuella nästlade objekt för tipset
        if (typeof choice === "object" && choice !== null) {
            // gissa på vanliga fält, annars stringify
            choice = choice.value ?? choice.guess ?? JSON.stringify(choice);
        }

        yourTip.textContent = "Ditt tips: " + (choice ?? "-");
        card.appendChild(yourTip);

        const resultDiv = document.createElement("div");
        resultDiv.className = "match-result";
<<<<<<< Updated upstream

=======
        
>>>>>>> Stashed changes
        let correct = tip.correctResult;
        // Hantera eventuella nästlade objekt för det rätta resultatet
        if (typeof correct === "object" && correct !== null) {
            correct = correct.value ?? correct.result ?? JSON.stringify(correct);
        }

        resultDiv.textContent = "Rätt resultat: " + (correct ?? "Ej klart");
        card.appendChild(resultDiv);

        container.appendChild(card);
        cards.push(card);
    });

    // Trigger för CSS-animationen
    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            cards.forEach(card => card.classList.add("show"));
        });
    });
}

<<<<<<< Updated upstream

=======
/**
 * Hämtar och visar den inloggade användarens profilinformation.
 */
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream

=======
    
>>>>>>> Stashed changes
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
            window.location.href = "login.html"; // Jag justerade '/login.html' till 'login.html' för att matcha era övriga filer
        });
}

/**
 * Sätter upp mjuka övergångar (fade) när användaren klickar på standardlänkar.
 */
function setupPageTransitions() {
    document.querySelectorAll("a").forEach(link => {
        link.addEventListener("click", e => {
            const url = link.getAttribute("href");

<<<<<<< Updated upstream
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
=======
            // Ignorera tomma länkar eller ankarlänkar
            if (!url || url.startsWith("#")) return;

            e.preventDefault();
            document.body.classList.add("fade-out");

            setTimeout(() => {
                window.location = url;
            }, 350);
        });
    });
}
>>>>>>> Stashed changes
