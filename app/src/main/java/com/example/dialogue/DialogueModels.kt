package com.example.dialogue

import androidx.annotation.DrawableRes
import com.example.R

/**
 * Character speaker definitions for the dialogue system.
 */
enum class CharacterSpeaker(
    val displayName: String,
    val title: String,
    @DrawableRes val portraitRes: Int,
    @DrawableRes val portraitExplainingRes: Int = portraitRes,
    @DrawableRes val portraitDefeatedRes: Int = portraitRes
) {
    CURATOR(
        displayName = "Marcel",
        title = "Curador do Esconderijo",
        portraitRes = R.drawable.marcel_default,
        portraitExplainingRes = R.drawable.marcel_explicando,
        portraitDefeatedRes = R.drawable.marcel_default
    ),
    ANTAGONIST(
        displayName = "Duque Von Poddle",
        title = "Antagonista",
        portraitRes = R.drawable.duquevonpoddle_default,
        portraitExplainingRes = R.drawable.duquevonpoddle_falando,
        portraitDefeatedRes = R.drawable.duquevonpoddle_derrotado
    ),
    NINA(
        displayName = "Nina",
        title = "Especialista em Mecânica",
        portraitRes = R.drawable.nina_default,
        portraitExplainingRes = R.drawable.nina_explicando,
        portraitDefeatedRes = R.drawable.nina_default
    ),
    LUNA(
        displayName = "Luna",
        title = "Avaliadora de Arte",
        portraitRes = R.drawable.luna_default,
        portraitExplainingRes = R.drawable.luna_default,
        portraitDefeatedRes = R.drawable.luna_default
    ),
    LEO(
        displayName = "Leo",
        title = "Arqueólogo",
        portraitRes = R.drawable.leo_default,
        portraitExplainingRes = R.drawable.leo_default,
        portraitDefeatedRes = R.drawable.leo_default
    )
}

/**
 * Single dialogue node inside a dialogue sequence.
 */
data class DialogueNode(
    val id: String,
    val speaker: CharacterSpeaker,
    val text: String,
    val actionText: String = "Continuar"
)

/**
 * Full dialogue sequence triggerable in different game contexts.
 */
data class DialogueSequence(
    val id: String,
    val nodes: List<DialogueNode>,
    val onCompleteTrigger: DialogueTrigger? = null
)

enum class DialogueTrigger {
    START_TUTORIAL_GAME,
    CLOSE_DIALOGUE
}

object GameDialogues {

    /**
     * Diálogo de Boas-Vindas (Curador) - Primeira vez abrindo o jogo.
     * Frases curtas, diretas e leves quebradas em múltiplos diálogos para leitura rápida.
     */
    val WELCOME_CURATOR_SEQUENCE = DialogueSequence(
        id = "welcome_curator_ftue",
        nodes = listOf(
            DialogueNode(
                id = "curator_welcome_1",
                speaker = CharacterSpeaker.CURATOR,
                text = "Ah, olá! Seja muito bem-vindo ao seu novo Esconderijo!",
                actionText = "Olá!"
            ),
            DialogueNode(
                id = "curator_welcome_2",
                speaker = CharacterSpeaker.CURATOR,
                text = "Como pode ver... o lugar está precisando de uma boa reforma.",
                actionText = "O que houve?"
            ),
            DialogueNode(
                id = "curator_welcome_3",
                speaker = CharacterSpeaker.CURATOR,
                text = "Mas não se preocupe! Eu sei exatamente como reviver este espaço...",
                actionText = "Como?"
            ),
            DialogueNode(
                id = "curator_welcome_4",
                speaker = CharacterSpeaker.CURATOR,
                text = "Com um pouco de ARTE de verdade e belas relíquias históricas!",
                actionText = "Como consigo?"
            ),
            DialogueNode(
                id = "curator_welcome_5",
                speaker = CharacterSpeaker.CURATOR,
                text = "Para resgatar as obras, precisamos resolver enigmas de placas e parafusos.",
                actionText = "Entendi!"
            ),
            DialogueNode(
                id = "curator_welcome_6",
                speaker = CharacterSpeaker.CURATOR,
                text = "Vamos começar com uma partida rápida para você pegar o jeito!",
                actionText = "Iniciar Partida"
            )
        ),
        onCompleteTrigger = DialogueTrigger.START_TUTORIAL_GAME
    )

    /**
     * Diálogo na Fase 1 da Primeira Saga (Mestre Vane / Antagonista).
     * Frases curtas e provocativas com ritmo dinâmico.
     */
    val ANTAGONIST_SAGA1_LEVEL1_SEQUENCE = DialogueSequence(
        id = "antagonist_saga1_level1",
        nodes = listOf(
            DialogueNode(
                id = "antagonist_intro_1",
                speaker = CharacterSpeaker.ANTAGONIST,
                text = "Ora, ora... então é você quem assumiu esta velha oficina?",
                actionText = "Quem é você?"
            ),
            DialogueNode(
                id = "antagonist_intro_2",
                speaker = CharacterSpeaker.ANTAGONIST,
                text = "Eu sou o Mestre Vane, o maior colecionador destas redondezas.",
                actionText = "O que quer?"
            ),
            DialogueNode(
                id = "antagonist_intro_3",
                speaker = CharacterSpeaker.ANTAGONIST,
                text = "Estas relíquias e placas pertencem a mim por direito.",
                actionText = "Veremos!"
            ),
            DialogueNode(
                id = "antagonist_intro_4",
                speaker = CharacterSpeaker.ANTAGONIST,
                text = "Acha mesmo que consegue soltar meus parafusos sem travar a doca?",
                actionText = "Com certeza!"
            ),
            DialogueNode(
                id = "antagonist_intro_5",
                speaker = CharacterSpeaker.ANTAGONIST,
                text = "Hmph! Não me faça rir. Mostre do que é capaz!",
                actionText = "Aceitar Desafio"
            )
        ),
        onCompleteTrigger = DialogueTrigger.CLOSE_DIALOGUE
    )
}
