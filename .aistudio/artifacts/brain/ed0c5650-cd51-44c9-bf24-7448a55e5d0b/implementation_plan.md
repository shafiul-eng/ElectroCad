# ElectroCAD – Professional EDA Circuit Designer & Dual-Channel Oscilloscope

A comprehensive overhaul of ElectroCAD into an authentic, industry-grade Electronic Design Automation (EDA) and circuit simulation suite inspired by Altium Designer and Proteus VSM. This upgrade introduces explicit component polarity indicators (+/- and Anode/Cathode) across schematic symbols and terminals, implements reverse-bias simulation physics, removes all AI references/chatbots, and introduces a dedicated Dual-Channel Oscilloscope and Waveform Analyzer instrument.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed by the user during the initial consultation:
> 1. **Component Polarity Representation**: Explicit color-coded terminal pins (Red `+` for positive/anode, Slate-Blue `-` for negative/cathode) combined with high-contrast schematic markings (`+`, `-`, `A`, `K`) engraved directly on components.
> 2. **Professional Instrument Replacement**: Complete elimination of all AI branding, assistants, and chatbots; replaced with a full-featured **Dual-Channel Oscilloscope & Waveform Analyzer** suite (CH1, CH2, Time/Div, Volts/Div, Trigger, FFT/Frequency readout).
> 3. **UI Aesthetic**: Professional **Dark EDA Workstation** interface (obsidian matte background `#0B0F19`, reticle coordinate CAD grid, phosphor signal traces, and monospaced engineering telemetry).

---

## 1. Overview & Core Concept

- **What It Does**: A workstation-class electronic CAD suite that combines a real-time SPICE-inspired Kirchhoff circuit engine, multi-touch component placement, terminal wiring, and virtual laboratory instruments.
- **Target Audience**: Electrical engineering students, professors, hardware hackers, and electronics professionals designing and debugging analog/digital circuits.
- **Key Value**: Delivers genuine engineering rigor with explicit polarity rules (preventing burned components and reverse-bias anomalies) and bench instrument telemetry without any AI gimmicks or artificial placeholders.

---

## 2. User Experience & Visual Design

### 2.1 Polarity System & Schematic Conventions
- **Explicit Terminal Markers**:
  - **Positive / Anode Terminals**: Vibrant Ruby Red terminal pin with bold `+` identifier (or `A` on diodes).
  - **Negative / Cathode Terminals**: Deep Slate Blue terminal pin with bold `-` identifier (or `K` on diodes).
  - **Electrolytic Capacitors**: Distinct curved negative plate with embossed minus stripes and a `+` badge on the flat anode plate.
  - **DC Power Supplies & Batteries**: Prominent `+` and `-` plates with terminal voltage ratings.
  - **Diodes & LEDs**: Clear triangular anode barrier pointing toward the cathode bar, plus flat edge cathode notch and photon emission arrows.
  - **Meters & Instruments**: Red positive probe port and Black/Blue negative common reference port.
- **Reverse Polarity Physics**:
  - Reverse-biased diodes and LEDs block current flow ($I = 0\text{ mA}$) and display an amber warning badge when reverse voltage is applied.
  - Reverse-connected DC voltmeters report negative differential voltages (e.g., $-9.00\text{ V}$), mirroring real-world DMMs.

### 2.2 Visual Identity & Dark EDA Theme
- **Color Palette**:
  - `EdaCanvasBg`: `#0B0F19` (Obsidian Matte CAD workspace)
  - `EdaGridDot`: `#1E293B` (Reticle precision coordinate grid)
  - `TraceActive`: `#00E5FF` (Cyan conductive signal trace)
  - `TracePositive`: `#EF4444` (VCC / Positive rail)
  - `TraceNegative`: `#3B82F6` (GND / Negative rail)
  - `ScopeCh1`: `#00E5FF` (Channel 1 Phosphor Cyan)
  - `ScopeCh2`: `#FBBF24` (Channel 2 Signal Amber)
  - `EdaSurface`: `#111827` (Matte instrument panel)
  - `EdaBorder`: `#1F2937` (Precision chamfered border)
- **Typography & Layout**:
  - Monospaced numeric engineering readouts (e.g. `12.00 V`, `36.36 mA`, `436.4 mW`, `1.00 kHz`).
  - Chamfered instrument dials, Time/Div and Volts/Div selector knobs, and oscilloscope screen graticule (8x10 divisions).

### 2.3 Tab Structure & Complete AI Removal
- **Tab 1: Schematics / ক্যানভাস (CAD Simulator)**: Interactive schematic designer with snapping grid, polarity badges, wire cutting/splicing, and Room DB design manager.
- **Tab 2: Oscilloscope / অসিলোস্কোপ (Waveform Analyzer)**: Real-time dual-channel oscilloscope with probe nodes, Timebase dial ($0.1\text{ ms}$ to $500\text{ ms}$/div), Volts/Div scale ($10\text{ mV}$ to $10\text{ V}$/div), Trigger modes, $V_{pp}$, $V_{rms}$, and frequency counter.
- **Tab 3: Calculations / ক্যালকুলেটর (Engineering Toolkit)**: Ohm's law, resistor color code decoder, 555 astable frequency calculator, voltage divider solver, and RC filter cut-off tool.
- **Tab 4: Lab Tutorials / টিউটোরিয়াল (Schematic Reference)**: Interactive circuit theory guides (RLC resonance, transistor biasing, op-amp configurations, logic gate truth tables).
- **Tab 5: Lab Quiz / কুইজ ল্যাব (Certification Lab)**: Interactive circuit analysis questions for students and educators.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Polarity-Aware Circuit Engine
- **Approach**: Enhance `CircuitEngine.solve()` to evaluate terminal polarity orientation for directional components (LED, Diode, Zener, Capacitor, Voltmeter, DC Battery).
- **Why**: Eliminates non-physical behavior where an LED illuminated regardless of wire orientation, teaching authentic circuit design rules.

### Decision 2: Dual-Channel Digital Storage Oscilloscope (DSO)
- **Approach**: Implement an authentic 8x10 division oscilloscope screen rendered on Compose Canvas with CH1/CH2 voltage probes, timebase sweep, triggering, AC/DC coupling, and harmonic frequency detection.
- **Why**: Replaces the generic AI chatbot with a genuine, high-utility testbench instrument that elevates the app into professional workstation territory.

---

## 4. Technical Architecture & Component Mapping

```
┌────────────────────────────────────────────────────────────────────────┐
│                   ElectroCAD EDA Workstation Architecture              │
└────────────────────────────────────────────────────────────────────────┘
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         ▼                          ▼                          ▼
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│ SimulatorScreen  │       │OscilloscopeScreen│       │CalculatorScreen  │
│  - CAD Canvas    │       │  - 8x10 Graticule│       │  - Ohm's Toolkit │
│  - Polarity Pins │       │  - CH1 & CH2 In  │       │  - RC / 555 Math │
│  - Smart Splicer │       │  - Time/Div Base │       │  - Color Codes   │
│  - Room DB Entity│       │  - Vpp / Vrms / f│       │  - Truth Tables  │
└──────────────────┘       └──────────────────┘       └──────────────────┘
         │                          │                          │
         └──────────────────────────┼──────────────────────────┘
                                    ▼
                   ┌───────────────────────────────────┐
                   │ CircuitEngine & SimulationState   │
                   │  - Polarity & Reverse Bias Check  │
                   │  - Ohm / Kirchhoff Matrix Solver  │
                   │  - Waveform Time-Series Buffer    │
                   └───────────────────────────────────┘
                                    │
                                    ▼
                   ┌───────────────────────────────────┐
                   │ Room Database: AppDatabase        │
                   │  - CircuitDesignEntity & DAO      │
                   └───────────────────────────────────┘
```

### Component & File Mapping:
1. `model/CircuitModel.kt`:
   - Update `Pin` model with `polarity: String` (`"+"`, `"-"`, `"A"`, `"K"`, `"GND"`).
   - Update `ComponentType` pins to assign exact polarity across all components.
2. `engine/CircuitEngine.kt`:
   - Enforce directional current checks: LEDs/Diodes only conduct when $V_{\text{anode}} > V_{\text{cathode}} + V_f$.
   - Reverse voltmeter connection displays signed negative voltage.
3. `ui/components/CircuitCanvas.kt`:
   - Draw high-contrast polarity symbols on the schematic body (e.g. `+` / `-` embossed on batteries, capacitors, meters, and diodes).
   - Render color-coded terminal pin badges (Red `#EF4444` with `+`, Slate-Blue `#3B82F6` with `-`).
4. `ui/screens/OscilloscopeScreen.kt`:
   - Brand-new dual-channel virtual DSO with oscilloscope graticule, CH1/CH2 signal generators linked to the active circuit, trigger controls, and frequency/voltage telemetry.
5. `ui/navigation/AppNavigation.kt`:
   - Replace `AI_GURU` tab with `OSCILLOSCOPE` ("অসিলোস্কোপ", "Oscilloscope").
   - Eliminate all references to AI in labels, navigation items, and headers.
