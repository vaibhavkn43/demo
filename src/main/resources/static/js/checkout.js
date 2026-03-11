let appliedCoupon = null;

let basePrice = window.BASE_PRICE;         // ₹49
let couponDiscount = window.COUPON_DISCOUNT; // ₹10
let validCoupon = window.COUPON_CODE;

let finalAmount = basePrice;

document.getElementById("payBtn").onclick = function () {

    let url = '/api/payment/create-order';

    if (appliedCoupon) {
        url += '?coupon=' + appliedCoupon;
    }

    fetch(url, {method: 'POST'})
        .then(res => res.json())
        .then(data => {

            var options = {
                "key": window.RAZORPAY_KEY,
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

            document.getElementById("payBtn").style.display = "none";

            window.location.reload();

        } else {
            alert("Payment verification failed");
        }
    });
}

function applyCoupon() {

    const code = document.getElementById("couponInput")
        .value.trim().toUpperCase();

    const msgEl = document.getElementById("couponMsg");
    const originalPriceEl = document.getElementById("originalPrice");
    const finalPriceEl = document.getElementById("finalPrice");
    const payBtn = document.getElementById("payBtn");

    if (code === validCoupon) {

        appliedCoupon = code;
        finalAmount = basePrice - couponDiscount;

        msgEl.innerText = "✅ Coupon applied!";
        msgEl.classList.remove("text-red-500");
        msgEl.classList.add("text-green-600");

        originalPriceEl.classList.remove("hidden");
        finalPriceEl.innerText = "₹" + (finalAmount);

        payBtn.innerText =
            "💳 Now Pay ₹" + (finalAmount) + " & Download Instantly";

    }

    else if (code === "") {

        appliedCoupon = null;
        finalAmount = basePrice;

        msgEl.innerText = "";
        originalPriceEl.classList.add("hidden");

        finalPriceEl.innerText = "₹" + (basePrice);

        payBtn.innerText =
            "💳 Pay ₹" + (basePrice) + " & Download Instantly";
    }

    else {

        appliedCoupon = null;
        finalAmount = basePrice;

        msgEl.innerText = "❌ Invalid coupon code";
        msgEl.classList.remove("text-green-600");
        msgEl.classList.add("text-red-500");

        originalPriceEl.classList.add("hidden");

        finalPriceEl.innerText = "₹" + (basePrice);

        payBtn.innerText =
            "💳 Pay ₹" + (basePrice) + " & Download Instantly";
    }
}