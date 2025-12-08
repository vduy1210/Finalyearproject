# Project Context Analysis: Coffee Shop POS System

This document analyzes the project context based on the **9 Types of Information Collection** framework. It serves as the foundation for the Rich Picture and System Design.

## 1. STAKEHOLDERS (Các bên liên quan)
*Who is involved and affected?*

*   **👤 Shop Owner (Internal/Decision Maker):**
    *   **Role:** Manages the business, finances, and inventory.
    *   **Interest:** Profitability, efficiency, data visibility.
    *   **Power:** High (Makes all purchasing and process decisions).
*   **👔 Staff / Barista (Internal/User):**
    *   **Role:** Takes orders, prepares drinks, serves customers.
    *   **Interest:** Ease of use, speed, reduced errors/stress.
    *   **Daily Work:** Heavy interaction with the system.
*   **👥 Customers (External/User):**
    *   **Role:** Buys coffee (Walk-in & Online).
    *   **Interest:** Fast service, accurate orders, convenience, rewards.
*   **🚚 Suppliers (External/Partner):**
    *   **Role:** Provides coffee beans and ingredients.
    *   **Interest:** Timely orders, clear communication.

## 2. PROBLEMS & ISSUES (Vấn đề hiện tại)
*What is broken?*

*   **⚠️ Manual Chaos:** Orders are written on paper, leading to illegible notes and lost tickets.
*   **❌ High Error Rate:** Wrong drinks made due to miscommunication between cashier and barista.
*   **😓 Staff Stress:** Overwhelmed during rush hours (7-9 AM); shouting orders creates a bad atmosphere.
*   **💸 Revenue Leakage:** Owner spends 2 hours/night counting cash; inventory discrepancies go unnoticed.
*   **📉 Customer Churn:** Long wait times (15+ mins) drive customers to competitors (Highlands, Starbucks).
*   **📝 Lack of Data:** "I don't know what sells best" – decisions are based on guesswork, not facts.

## 3. PROCESSES (Quy trình nghiệp vụ)
*How does work happen?*

*   **🔄 Ordering (Core):** Customer orders -> Staff writes on paper -> Paper passed to Kitchen -> Payment taken manually.
*   **📊 Inventory (Management):** Staff checks shelves visually -> Writes shopping list -> Owner calls Supplier (often late).
*   **⚙️ Reporting (Management):** Owner manually tallies paper receipts at end of day -> Enters into Excel/Notebook.

## 4. RELATIONSHIPS & INTERACTIONS (Mối quan hệ)
*Who interacts with whom?*

*   **Customer ↔️ Staff:** Verbal ordering, payment negotiation (cash/transfer). *Tension: Wait times.*
*   **Staff ⚡ Kitchen:** Passing paper tickets. *Conflict: Unreadable handwriting.*
*   **Owner ⚡ Staff:** Assigning shifts, checking cash. *Tension: Trust issues with cash.*
*   **Owner ↔️ Supplier:** Phone calls for orders. *Conflict: Last-minute panic orders.*

## 5. CONTEXT & ENVIRONMENT (Bối cảnh)
*Where does this happen?*

*   **Business Context:**
    *   **Type:** Small, independent coffee shop.
    *   **Location:** Da Nang, Vietnam.
    *   **Scale:** Family-owned, 3-4 staff per shift.
    *   **Traffic:** 50-100 customers/day. Peak: 7:00 - 9:00 AM.
*   **External Factors:**
    *   **Competitors:** Aggressive chains (Highlands, Phuc Long) offering apps and loyalty points.
    *   **Trend:** Customers expect "Scan to Pay" (QR) and online ordering.

## 6. GOALS & OBJECTIVES (Mục tiêu)
*What do we want to achieve?*

*   **🎯 Strategic (Long-term):** Increase Revenue by **30%** within 6 months.
*   **📊 Tactical (Medium-term):** Build a loyal customer base via a Points System.
*   **✅ Operational (Short-term):**
    *   Reduce service time from 15 mins to **5 mins**.
    *   Eliminate order errors (0% error rate).
    *   Automate end-of-day reporting (Instant).

## 7. CONSTRAINTS & LIMITATIONS (Ràng buộc)
*What holds us back?*

*   **💰 Budget:** Limited (Small business). Cannot afford expensive enterprise ERPs (like SAP/Oracle).
*   **📡 Infrastructure:** Internet connection in Da Nang can be unstable (Need **Offline Mode**).
*   **👥 People:** Staff are students/part-time; high turnover. System must be **easy to learn** (max 15 min training).
*   **🔧 Hardware:** Must run on existing devices (Owner's iPad, Staff's Android phones) to save costs.

## 8. DATA & INFORMATION FLOWS (Luồng dữ liệu)
*What information moves?*

*   **Order Data:** Currently Paper (Lost easily) → Future: Digital Stream (App -> Server -> Kitchen Screen).
*   **Inventory Data:** Currently Mental/Notebook → Future: Auto-deduction from database upon sale.
*   **Customer Data:** Currently None (Anonymous) → Future: Phone numbers, Order History, Points.
*   **Sales Reports:** Currently Manual Calculation → Future: Real-time Dashboards.

## 9. CONCERNS & WORRIES (Lo ngại)
*What keeps them up at night?*

*   **😰 Owner:** "Will the system be too hard to use? Will staff steal cash if I'm not there?"
*   **🤔 Staff:** "Will this make me work harder? Will I get in trouble if I press the wrong button?"
*   **❓ Customers:** "Is my data safe? Is the app annoying to install?"
*   **🔧 Technical:** "What happens if the Wi-Fi dies during a rush?" (Addressed by Offline Mode).
