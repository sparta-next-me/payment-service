// success.js (수정됨)

// --- 1. URL 파라미터 획득 및 변환 ---
const urlParams = new URLSearchParams(window.location.search);
const paymentKey = urlParams.get("paymentKey");
const orderId = urlParams.get("orderId");
const amount = parseInt(urlParams.get("amount")); // 💡 long 타입에 맞게 parseInt 사용

// --- 2. HTML 요소 참조 및 정보 표시 ---
const paymentKeyElement = document.getElementById("paymentKey");
const orderIdElement = document.getElementById("orderId");
const amountElement = document.getElementById("amount");

if (paymentKeyElement) paymentKeyElement.textContent = paymentKey;
if (orderIdElement) orderIdElement.textContent = orderId;
if (amountElement) amountElement.textContent = `${amount}원`;

const confirmLoadingSection = document.querySelector('.confirm-loading');
const confirmSuccessSection = document.querySelector('.confirm-success');

// --- 3. 결제 승인 로직 ---
async function confirmPayment() {
    // 💡 필수 파라미터 확인 (안정성)
    if (!paymentKey || !orderId || isNaN(amount)) {
        alert("❌ 결제 승인 실패: 필수 정보가 누락되었습니다.");
        confirmLoadingSection.style.display = 'none'; // 로딩 숨기기
        return;
    }

    try {
        const response = await fetch('/v1/payments/confirm', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                paymentKey,
                orderId,
                amount: amount, // 💡 이미 parseInt로 변환했으므로 amount 사용
            }),
        });

        if (!response.ok) {
            const errText = await response.text();
            alert("결제 승인 실패 ❌\n" + errText);
            console.error(errText);
            return;
        }

        console.log("✅ 결제 승인 성공!");
        confirmLoadingSection.style.display = 'none';
        confirmSuccessSection.style.display = 'flex';
    } catch (e) {
        console.error("❌ 결제 승인 중 오류:", e);
        alert("서버 요청 실패: " + e.message);
    }
}

// --- 4. 함수 호출 시점 변경 (자동 실행) ---
// const confirmPaymentButton = document.getElementById('confirmPaymentButton');
// confirmPaymentButton.addEventListener('click', confirmPayment); // 💡 이 부분 주석 또는 삭제

confirmPayment(); // 💡 페이지 로드 시 바로 결제 승인 요청!


/*const urlParams = new URLSearchParams(window.location.search);
const paymentKey = urlParams.get("paymentKey");
const orderId = urlParams.get("orderId");
const amount = urlParams.get("amount");

const paymentKeyElement = document.getElementById("paymentKey");
const orderIdElement = document.getElementById("orderId");
const amountElement = document.getElementById("amount");

if (paymentKeyElement) paymentKeyElement.textContent = paymentKey;
if (orderIdElement) orderIdElement.textContent = orderId;
if (amountElement) amountElement.textContent = `${amount}원`;

const confirmLoadingSection = document.querySelector('.confirm-loading');
const confirmSuccessSection = document.querySelector('.confirm-success');

async function confirmPayment() {
    try {
        const response = await fetch('/v1/payments/confirm', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                paymentKey,
                orderId,
                amount: Number(amount),
            }),
        });

        if (!response.ok) {
            const errText = await response.text();
            alert("결제 승인 실패 ❌\n" + errText);
            console.error(errText);
            return;
        }

        console.log("✅ 결제 승인 성공!");
        confirmLoadingSection.style.display = 'none';
        confirmSuccessSection.style.display = 'flex';
    } catch (e) {
        console.error("❌ 결제 승인 중 오류:", e);
        alert("서버 요청 실패: " + e.message);
    }
}

const confirmPaymentButton = document.getElementById('confirmPaymentButton');
confirmPaymentButton.addEventListener('click', confirmPayment);*/
