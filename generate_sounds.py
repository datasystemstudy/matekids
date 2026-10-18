import wave
import math
import struct
import os

SAMPLE_RATE = 44100

def create_wave(duration, sample_generator, filename):
    num_samples = int(duration * SAMPLE_RATE)
    samples = []
    for i in range(num_samples):
        t = i / SAMPLE_RATE
        val = sample_generator(t, duration)
        # clamp between -1.0 and 1.0
        val = max(-1.0, min(1.0, val))
        samples.append(int(val * 32767))
    
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)  # Mono
        wav_file.setsampwidth(2)  # 16-bit
        wav_file.setframerate(SAMPLE_RATE)
        data = struct.pack(f'<{len(samples)}h', *samples)
        wav_file.writeframes(data)
    print(f"Generated {filename}")

def env_bell(t, start, duration):
    """Envolvente de campana / marimba con ataque rápido y decaimiento suave"""
    rel_t = t - start
    if rel_t < 0:
        return 0.0
    if rel_t > duration:
        return 0.0
    attack = min(1.0, rel_t / 0.015)
    decay = math.exp(-rel_t * 6.5)
    return attack * decay

def tone_rich(t, freq):
    """Genera tono con armónicos para sonido cálido y musical"""
    w1 = math.sin(2 * math.pi * freq * t)
    w2 = 0.35 * math.sin(2 * math.pi * freq * 2 * t)
    w3 = 0.15 * math.sin(2 * math.pi * freq * 3 * t)
    return (w1 + w2 + w3) / 1.5

# 1. ACERTAR (sound_correct.wav): Arpegio mayor ascendente brillante (C5 -> E5 -> G5 -> C6)
def gen_correct(t, dur):
    # Notas: C5=523.25, E5=659.25, G5=783.99, C6=1046.50
    notes = [
        (0.00, 0.35, 523.25, 0.5),
        (0.09, 0.35, 659.25, 0.6),
        (0.18, 0.40, 783.99, 0.7),
        (0.27, 0.50, 1046.50, 0.85)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        env = env_bell(t, start, note_dur)
        val += env * tone_rich(t, freq) * gain
    return val * 0.9

# 2. NO ACERTAR (sound_incorrect.wav): Toque suave, descendente, dulce y motivador (no punitivo)
def gen_incorrect(t, dur):
    # Dos tonos descendentes suaves tipo gota / marimba: F4 (349Hz) -> Db4 (277Hz)
    notes = [
        (0.00, 0.22, 349.23, 0.65),
        (0.12, 0.30, 277.18, 0.60)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = (min(1.0, rel_t / 0.02)) * math.exp(-rel_t * 8.0)
            # Ligero tono más cálido
            w = math.sin(2 * math.pi * freq * rel_t) + 0.2 * math.sin(4 * math.pi * freq * rel_t)
            val += env * w * gain
    return val * 0.85

# 3. CUMPLIR LA META / VICTORIA (sound_goal_complete.wav): Fanfarria triunfal de celebración infantil
def gen_goal_complete(t, dur):
    # Arpegio triunfal en cascada con final resonante:
    # G4(392) -> C5(523) -> E5(659) -> G5(784) -> C6(1046) -> Acorde final (C5+G5+C6)
    notes = [
        (0.00, 0.25, 392.00, 0.5),
        (0.10, 0.25, 523.25, 0.55),
        (0.20, 0.25, 659.25, 0.6),
        (0.30, 0.30, 783.99, 0.7),
        (0.42, 0.80, 1046.50, 0.8),
        # Armonía de soporte en el final
        (0.42, 0.75, 523.25, 0.4),
        (0.42, 0.75, 659.25, 0.4),
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.02) * math.exp(-rel_t * 3.8)
            # shimmery sparkle
            shimmer = 1.0 + 0.1 * math.sin(2 * math.pi * 8 * rel_t)
            w = tone_rich(rel_t, freq) * shimmer
            val += env * w * gain
    return val * 0.75

# 4. SUMA (sound_op_addition.wav): Pop saltarín alegre ascendente (D5 -> G5)
def gen_addition(t, dur):
    notes = [
        (0.00, 0.18, 587.33, 0.6),
        (0.08, 0.25, 783.99, 0.75)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.015) * math.exp(-rel_t * 9.0)
            val += env * tone_rich(rel_t, freq) * gain
    return val * 0.85

# 5. RESTA (sound_op_subtraction.wav): Deslizamiento divertido de madera / xilófono (E5 -> A5)
def gen_subtraction(t, dur):
    notes = [
        (0.00, 0.18, 659.25, 0.7),
        (0.08, 0.25, 880.00, 0.7)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.015) * math.exp(-rel_t * 9.0)
            val += env * (math.sin(2 * math.pi * freq * rel_t) + 0.3 * math.sin(4 * math.pi * freq * rel_t)) * gain
    return val * 0.85

# 6. MULTIPLICACIÓN (sound_op_multiplication.wav): Triple destello estelar mágico (D5 -> F#5 -> A5)
def gen_multiplication(t, dur):
    notes = [
        (0.00, 0.16, 587.33, 0.55),
        (0.06, 0.16, 739.99, 0.65),
        (0.12, 0.28, 880.00, 0.80)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.01) * math.exp(-rel_t * 8.0)
            val += env * tone_rich(rel_t, freq) * gain
    return val * 0.85

# 7. DIVISIÓN (sound_op_division.wav): Burbuja cristalina / gota de agua
def gen_division(t, dur):
    notes = [
        (0.00, 0.16, 783.99, 0.6),
        (0.06, 0.16, 659.25, 0.65),
        (0.12, 0.28, 1046.50, 0.8)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            # Ligero pitch bend hacia arriba para efecto gota/burbuja
            pitch_mod = 1.0 + 0.05 * math.sin(math.pi * rel_t / note_dur)
            env = min(1.0, rel_t / 0.01) * math.exp(-rel_t * 8.5)
            val += env * math.sin(2 * math.pi * (freq * pitch_mod) * rel_t) * gain
    return val * 0.85

# 8. CLICK GENERAL (sound_click.wav): Toque sutil y suave de madera
def gen_click(t, dur):
    if t > 0.05:
        return 0.0
    env = min(1.0, t / 0.005) * math.exp(-t * 80.0)
    return env * math.sin(2 * math.pi * 950 * t) * 0.6

# 9. PISTA / IDEA (sound_hint.wav): Campanilla mágica ascendente brillante (E5 -> B5)
def gen_hint(t, dur):
    notes = [
        (0.00, 0.25, 659.25, 0.6),
        (0.10, 0.40, 987.77, 0.75)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.01) * math.exp(-rel_t * 6.5)
            val += env * tone_rich(rel_t, freq) * gain
    return val * 0.85

# 10. COMENZAR JUEGO (sound_start.wav): Despegue alegre ascendente (C5 -> G5 -> C6)
def gen_start(t, dur):
    notes = [
        (0.00, 0.20, 523.25, 0.5),
        (0.08, 0.20, 783.99, 0.65),
        (0.16, 0.45, 1046.50, 0.8)
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.015) * math.exp(-rel_t * 5.5)
            val += env * tone_rich(rel_t, freq) * gain
    return val * 0.85

# 11. BIENVENIDA OFICIAL (sound_welcome.wav): Acorde celestial mágico y alegre de bienvenida a MateKids
def gen_welcome(t, dur):
    # Progresión ascendente estelar de campanas (C5 -> E5 -> G5 -> A5 -> C6 -> E6)
    # y acorde resonante sostenido con brillo mágico
    notes = [
        (0.00, 0.40, 523.25, 0.50),  # C5
        (0.12, 0.40, 659.25, 0.55),  # E5
        (0.24, 0.45, 783.99, 0.60),  # G5
        (0.36, 0.45, 880.00, 0.65),  # A5
        (0.48, 0.55, 1046.50, 0.80), # C6
        (0.60, 0.80, 1318.51, 0.85), # E6 (campana alta brillante)
        # Acorde armónico de fondo cálido al llegar a la cima
        (0.60, 0.75, 523.25, 0.40),  # C5 base
        (0.60, 0.75, 783.99, 0.45),  # G5 armonía
        (0.60, 0.75, 1046.50, 0.50)  # C6 octava
    ]
    val = 0.0
    for start, note_dur, freq, gain in notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.015) * math.exp(-rel_t * 4.2)
            # Brillo mágico con sutil modulación
            shimmer = 1.0 + 0.12 * math.sin(2 * math.pi * 9 * rel_t)
            w = tone_rich(rel_t, freq) * shimmer
            val += env * w * gain
    return val * 0.80

# 12. APLAUSOS DE RACHA (sound_applause.wav): Aplausos festivos y ovación de racha para MateKids
def init_applause():
    import random
    r = random.Random(42)
    claps = []
    # Generar 160 palmadas con densidad creciente y luego desvanecimiento
    for _ in range(160):
        t_start = r.uniform(0.01, 1.40)
        f_res = r.uniform(650, 1400)
        f_crack = r.uniform(1800, 3600)
        decay = r.uniform(60.0, 110.0)
        vol = r.uniform(0.35, 0.85) * max(0.1, 1.0 - (t_start / 1.5)**2)
        claps.append((t_start, f_res, f_crack, decay, vol))
    return claps

APPLAUSE_CLAPS = init_applause()

def gen_applause(t, dur):
    val = 0.0
    for t_start, f_res, f_crack, decay, vol in APPLAUSE_CLAPS:
        rel_t = t - t_start
        if 0 <= rel_t <= 0.06:
            env = min(1.0, rel_t / 0.003) * math.exp(-rel_t * decay)
            clap = math.sin(2 * math.pi * f_res * rel_t) * 0.65 + math.sin(2 * math.pi * f_crack * rel_t) * 0.35
            clap *= (1.0 + 0.5 * math.sin(2 * math.pi * 5320 * rel_t))
            val += env * clap * vol
    
    # Armonía triunfal de celebración de fondo (acorde de racha C5 -> G5 -> C6)
    fanfare_notes = [
        (0.00, 0.30, 523.25, 0.25),
        (0.12, 0.30, 659.25, 0.30),
        (0.24, 0.50, 783.99, 0.35),
        (0.36, 0.90, 1046.50, 0.45)
    ]
    for start, note_dur, freq, gain in fanfare_notes:
        rel_t = t - start
        if 0 <= rel_t <= note_dur:
            env = min(1.0, rel_t / 0.02) * math.exp(-rel_t * 4.0)
            val += env * tone_rich(rel_t, freq) * gain

    return val * 0.70

out_dir = "app/src/main/res/raw"
os.makedirs(out_dir, exist_ok=True)

create_wave(0.65, gen_correct, os.path.join(out_dir, "sound_correct.wav"))
create_wave(0.45, gen_incorrect, os.path.join(out_dir, "sound_incorrect.wav"))
create_wave(1.25, gen_goal_complete, os.path.join(out_dir, "sound_goal_complete.wav"))
create_wave(0.35, gen_addition, os.path.join(out_dir, "sound_op_addition.wav"))
create_wave(0.35, gen_subtraction, os.path.join(out_dir, "sound_op_subtraction.wav"))
create_wave(0.42, gen_multiplication, os.path.join(out_dir, "sound_op_multiplication.wav"))
create_wave(0.42, gen_division, os.path.join(out_dir, "sound_op_division.wav"))
create_wave(0.06, gen_click, os.path.join(out_dir, "sound_click.wav"))
create_wave(0.50, gen_hint, os.path.join(out_dir, "sound_hint.wav"))
create_wave(0.60, gen_start, os.path.join(out_dir, "sound_start.wav"))
create_wave(1.40, gen_welcome, os.path.join(out_dir, "sound_welcome.wav"))
create_wave(1.50, gen_applause, os.path.join(out_dir, "sound_applause.wav"))
print("All audio files successfully generated!")

