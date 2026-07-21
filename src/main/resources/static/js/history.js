// Transfer history page — fetch and render GET /api/transfers newest first.

async function loadTransfers() {
  const rows = document.getElementById('rows');
  const status = document.getElementById('status');
  try {
    const res = await fetch('/api/transfers');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const transfers = await res.json();

    if (transfers.length === 0) {
      rows.innerHTML = '<tr><td colspan="7" class="status">No transfers found.</td></tr>';
      status.textContent = 'No conversions have been recorded yet.';
      status.classList.remove('err');
      return;
    }

    rows.innerHTML = transfers.map(t => `
      <tr>
        <td class="mono">${t.id}</td>
        <td class="mono">${t.fromAccount}</td>
        <td class="mono">${t.toAccount}</td>
        <td class="num">${Number(t.amount).toFixed(2)}</td>
        <td class="mono">${t.currency}</td>
        <td class="mono">${t.executedAt}</td>
        <td class="mono">${t.status}</td>
      </tr>`).join('');

    status.textContent = `${transfers.length} transfers loaded.`;
    status.classList.remove('err');
  } catch (err) {
    rows.innerHTML = '<tr><td colspan="7" class="status err">Could not load transfer history.</td></tr>';
    status.textContent = 'Could not load history: ' + err.message;
    status.classList.add('err');
  }
}

loadTransfers();