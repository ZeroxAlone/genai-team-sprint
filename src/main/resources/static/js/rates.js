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
