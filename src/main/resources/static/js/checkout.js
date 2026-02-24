let appliedCoupon = null;
let finalAmount = 4900; // default

document.getElementById("payBtn").onclick = function () {

    let url = '/api/payment/create-order';

    if (appliedCoupon) {
        url += '?coupon=' + appliedCoupon;
    }

    fetch(url, {method: 'POST'})
            .then(res => res.json())
            .then(data => {

                var options = {
                    "key": "rzp_test_SJuyZYsz9XMyiw",
                    "amount": data.amount,
                    "currency": "INR",
                    "name": "Biodata Maker",
                    "description": "PDF Download",
                    "order_id": data.orderId,

                    "handler": function (response) {
                        verifyPayment(response);
                    }
                };

                var rzp = new Razorpay(options);
                rzp.open();
            });
};


function verifyPayment(response) {
    fetch('/api/payment/verify-payment', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(response)
    })
            .then(res => res.text())
            .then(result => {
                if (result === "success") {

                    // 🔓 unlock buttons dynamically
                    document.getElementById("payBtn").style.display = "none";

                    // show download buttons
                    window.location.reload();

                    // OR directly trigger download
                    downloadPDF();

                } else {
                    alert("Payment verification failed");
                }
            });
}

function applyCoupon() {

    const code = document.getElementById("couponInput").value.trim().toUpperCase();

    const msgEl = document.getElementById("couponMsg");
    const originalPriceEl = document.getElementById("originalPrice");
    const finalPriceEl = document.getElementById("finalPrice");
    const payBtn = document.getElementById("payBtn");

    if (code === "NEW10") {

        appliedCoupon = code;
        finalAmount = 3900;

        // ✅ success message
        msgEl.innerText = "✅ Coupon applied! ₹10 OFF";
        msgEl.classList.remove("text-red-500");
        msgEl.classList.add("text-green-600");

        // ✅ show strike price
        originalPriceEl.classList.remove("hidden");

        // ✅ update price
        finalPriceEl.innerText = "₹39";

        // ✅ update button text
        payBtn.innerText = "💳 Now Pay Just ₹39 & Download Instantly";

    }
    if (code === "") {
        appliedCoupon = null;
        finalAmount = 4900;

        msgEl.innerText = "";
        originalPriceEl.classList.add("hidden");
        finalPriceEl.innerText = "₹49";
        payBtn.innerText = "💳 Pay ₹49 & Download Instantly";
        return;
    } else {

        appliedCoupon = null;
        finalAmount = 4900;

        // ❌ error message
        msgEl.innerText = "❌ Invalid coupon code";
        msgEl.classList.remove("text-green-600");
        msgEl.classList.add("text-red-500");

        // 🔁 RESET UI BACK
        originalPriceEl.classList.add("hidden");
        finalPriceEl.innerText = "₹49";
        payBtn.innerText = "💳 Pay ₹49 & Download Instantly";
    }
}

function applyCoupon() {

    const code = document.getElementById("couponInput").value.trim().toUpperCase();

    const msgEl = document.getElementById("couponMsg");
    const originalPriceEl = document.getElementById("originalPrice");
    const finalPriceEl = document.getElementById("finalPrice");
    const payBtn = document.getElementById("payBtn");

    // 🎯 CASE 1: Valid Coupon
    if (code === "NEW10") {

        appliedCoupon = code;
        finalAmount = 3900;

        msgEl.innerText = "✅ Coupon applied! ₹10 OFF";
        msgEl.classList.remove("text-red-500");
        msgEl.classList.add("text-green-600");

        originalPriceEl.classList.remove("hidden");
        finalPriceEl.innerText = "₹39";

        payBtn.innerText = "💳 Now Pay Just ₹39 & Download Instantly";

    }

    // 🎯 CASE 2: Empty Input (Reset)
    else if (code === "") {

        appliedCoupon = null;
        finalAmount = 4900;

        msgEl.innerText = "";
        originalPriceEl.classList.add("hidden");
        finalPriceEl.innerText = "₹49";

        payBtn.innerText = "💳 Pay ₹49 & Download Instantly";

    }

    // 🎯 CASE 3: Invalid Coupon
    else {

        appliedCoupon = null;
        finalAmount = 4900;

        msgEl.innerText = "❌ Invalid coupon code";
        msgEl.classList.remove("text-green-600");
        msgEl.classList.add("text-red-500");

        originalPriceEl.classList.add("hidden");
        finalPriceEl.innerText = "₹49";

        payBtn.innerText = "💳 Pay ₹49 & Download Instantly";
    }
}