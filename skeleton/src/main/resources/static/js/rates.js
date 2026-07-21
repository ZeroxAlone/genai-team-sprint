// Rates feature — GET /api/rates and render the latest exchange rates table.
// Pattern: fetch the API, render rows, handle empty DB (200 with []).

async function loadRates() {
  const rows = document.getElementById('rows');
  const status = document.getElementById('status');
  try {
    const res = await fetch('/api/rates');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const rates = await res.json();

    if (rates.length === 0) {
      rows.innerHTML = '<tr><td colspan="4" class="status">No rates found.</td></tr>';
      return;
    }

    rows.innerHTML = rates.map(r => `
      <tr>
        <td class="mono">${r.base}</td>
        <td class="mono">${r.quote}</td>
        <td class="num">${r.rate}</td>
        <td class="mono">${r.rateDate}</td>
      </tr>`).join('');
    status.textContent = `${rates.length} rates loaded from the database.`;
    status.classList.remove('err');
  } catch (err) {
    rows.innerHTML = '<tr><td colspan="4" class="status err">Could not load rates.</td></tr>';
    status.textContent = 'Is the app running and the database seeded? Try /api/health/db. (' + err.message + ')';
    status.classList.add('err');
  }
}

loadRates();

// --- Single-pair lookup: GET /api/rates/{base}/{quote} (02-pair-lookup) ---
async function lookupPair(event) {
  event.preventDefault();
  const form = event.target;
  const result = document.getElementById('lookup-result');
  const base = form.base.value.trim().toUpperCase();
  const quote = form.quote.value.trim().toUpperCase();
  try {
    const res = await fetch(`/api/rates/${base}/${quote}`);
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || ('HTTP ' + res.status));
    }
    const rate = await res.json();
    result.textContent = `${rate.base}/${rate.quote} = ${rate.rate} (as of ${rate.rateDate})`;
    result.classList.remove('err');
  } catch (err) {
    result.textContent = 'Could not look up pair: ' + err.message;
    result.classList.add('err');
  }
}

document.getElementById('lookup-form').addEventListener('submit', lookupPair);
