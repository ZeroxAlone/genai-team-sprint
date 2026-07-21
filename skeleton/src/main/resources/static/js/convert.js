// Convert page - calls GET /api/convert and renders the result table.

async function doConvert(event) {
  event.preventDefault();
  const form = event.target;
  const formStatus = document.getElementById('form-status');
  const resultPanel = document.getElementById('result-panel');
  const resultRows = document.getElementById('result-rows');

  formStatus.textContent = '';
  formStatus.classList.remove('err');
  resultPanel.style.display = 'none';

  const base = form.base.value;
  const quote = form.quote.value;
  const amount = form.amount.value;

  if (base === quote) {
    formStatus.textContent = 'Please choose two different currencies.';
    formStatus.classList.add('err');
    return;
  }

  try {
    const url = `/api/convert?base=${encodeURIComponent(base)}&quote=${encodeURIComponent(quote)}&amount=${encodeURIComponent(amount)}`;
    const res = await fetch(url);
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || ('HTTP ' + res.status));
    }
    const data = await res.json();

    resultRows.innerHTML = `
      <tr><td>Amount</td><td class="mono">${fmt(data.amount)} ${base}</td></tr>
      <tr><td>Rate</td><td class="mono">${data.rate}</td></tr>
      <tr><td>Converted</td><td class="mono">${fmt(data.converted)} ${quote}</td></tr>
      <tr><td>Fee</td><td class="mono">${fmt(data.fee)} ${quote}</td></tr>
      <tr><td><strong>Total</strong></td><td class="mono"><strong>${fmt(data.total)} ${quote}</strong></td></tr>
    `;
    resultPanel.style.display = '';
  } catch (err) {
    formStatus.textContent = 'Could not convert: ' + err.message;
    formStatus.classList.add('err');
  }
}

function fmt(n) {
  return Number(n).toFixed(2);
}

document.getElementById('convert-form').addEventListener('submit', doConvert);
