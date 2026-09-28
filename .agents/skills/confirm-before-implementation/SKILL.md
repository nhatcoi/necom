---
name: confirm-before-implementation
description: >-
  Enforces a confirmation-first workflow before modifying files, creating code, or executing state-changing operations.
  Use whenever a user asks questions combined with requests to fix, implement, modify, or refactor code. Ensures that
  investigation results, root-cause explanations, and proposed solutions are presented first, and the agent pauses to
  ask for explicit user confirmation before applying any changes.
---

# Confirm Before Implementation Skill

## 1. Overview & Core Philosophy

By default, an AI agent might immediately jump to editing files or executing commands as soon as words like "fix", "implement", or "update" appear in the user prompt. 

This skill enforces a strict **"Analyze & Confirm Before Execution"** protocol:
- **Never modify code or system state on the first turn of a diagnostic or explanatory request.**
- Thoroughly investigate using read-only operations.
- Clearly answer all user questions and explain the root causes of any problems.
- Propose a clear, structured solution plan.
- **Stop and ask for user confirmation** before taking any implementation action.

---

## 2. When to Apply

Apply this workflow whenever:
1. The user combines an informational inquiry or diagnosis with an action prompt (e.g., *"Why does X fail? Is Y using A or B? Fix X."*).
2. The user asks to fix an issue, refactor a module, or implement a feature where multiple approaches exist or side-effects are possible.
3. The user wants to understand the problem and approve the approach before code files are edited.

---

## 3. The 5-Step Workflow Protocol

```
[ User Request ]
       │
       ▼
1. Read-Only Investigation ────► (Inspect files, grep, read configs — NO writes/edits)
       │
       ▼
2. Direct Answers & Root Cause ─► (Answer all factual questions; explain the "why")
       │
       ▼
3. Solution Proposal & Plan ───► (Show exact steps, affected files, preview diff/logic)
       │
       ▼
4. STOP & Ask for Confirmation ─► (Ask the user: "Would you like me to implement this now?")
       │
       ▼
[ Wait for User Response ]
       │
       ├─► If User says "YES / Proceed" ──► 5. Execute Implementation & Verify
       └─► If User says "NO / Change plan" ─► Adjust proposal, do not touch code
```

### Step 1: Read-Only Investigation
- Inspect relevant codebase files using read-only tools (`view_file`, code search, MCP graph tools).
- Never call file-modifying tools (`write_to_file`, `replace_file_content`) or destructive shell commands during this phase.
- Gather accurate facts: inspect data structures, encoding formats, dependencies, configuration values, or database schemas.

### Step 2: Answer Inquiries & Explain Causes
Before discussing fixes:
- **Directly answer each specific question** asked in the prompt (e.g., status, version, config values, data states).
- **Explain the root cause** clearly and concisely (e.g., why an encoding failed, why a race condition happened, why a dependency mismatched).

### Step 3: Present Proposed Solution & Plan
Detail the proposed remediation clearly:
- Files to be modified or created.
- High-level logic or code snippet preview.
- Potential risks, trade-offs, or dependencies.

### Step 4: The Confirmation Gate (MANDATORY STOP)
- **DO NOT** perform any file writes or mutations in this response.
- End the response with a clear, polite call-to-action asking for approval.
- You may use the `ask_question` tool or a direct interactive prompt such as:
  > *"Would you like me to proceed with implementing this fix now?"*

### Step 5: Execution (Only After Approval)
- Once the user explicitly approves (e.g., *"Yes"*, *"Proceed"*, *"Go ahead"*):
  1. Perform the edits using targeted tools (`replace_file_content`, `write_to_file`).
  2. Verify changes (run linter, test suite, or build script if applicable).
  3. Report completed work and verification results back to the user.

---

## 4. Reference Example

### User Prompt:
> *"What is the reason why the address has unicode text errors? Is the current address using the new 34 provinces or the old 63 provinces? Fix the text error."*

### Agent Response Pattern:

1. **Investigation (Internal Tool Calls)**:
   - Reads address validation or seed files.
   - Identifies text encoding mismatch (e.g., UTF-8 vs ISO-8859-1 or composite vs decomposed unicode / NFD vs NFC).
   - Checks the province list length and data sources to verify whether 34 or 63 provinces are configured.

2. **User-Facing Response**:
   > ### 1. Root Cause of Unicode Text Error
   > The address text error occurs because input strings are being stored in Unicode Decomposed form (NFD) while the database / font expects Precomposed form (NFC), causing diacritics to detach or display as garbled characters (e.g., `o` + `\u0300` instead of `ò`).
   >
   > ### 2. Current Address Data Status
   > The system currently uses the **63 provinces/cities** dataset defined in `src/constants/provinces.json`. It has not yet migrated to the 34 reorganized administrative units.
   >
   > ### 3. Proposed Fix
   > To resolve the unicode display error:
   > - Add a normalization step (`text.normalize('NFC')`) in `src/utils/addressFormatter.ts`.
   > - Update the database ingestion pipeline to sanitize all incoming province/district strings before persistence.
   >
   > ---
   > **Would you like me to proceed with implementing this fix in `src/utils/addressFormatter.ts` now?**

3. **Agent Action**:
   - Stops execution and waits for user confirmation.
   - **Zero file edits** are committed until the user replies.

---

## 5. Strict Constraints & Prohibitions

1. **NEVER assume implicit permission**: Words like "fix it", "make it work", or "refactor" in a prompt that also contains inquiries do NOT grant immediate execution permission under this skill.
2. **NO speculative writes**: Do not write temporary "draft" files or modify existing files "just in case".
3. **DO NOT skip answers**: Never jump straight to the solution without answering the user's analytical or factual questions first.
