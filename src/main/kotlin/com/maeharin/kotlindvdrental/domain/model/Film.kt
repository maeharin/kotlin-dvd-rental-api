package com.maeharin.kotlindvdrental.domain.model

import com.maeharin.kotlindvdrental.domain.command.FilmCreateCommand
import com.maeharin.kotlindvdrental.infrastructure.doma.entity.FilmEntity
import com.maeharin.kotlindvdrental.infrastructure.doma.entity.FilmWithRelationEntity
import java.math.BigDecimal
import java.time.LocalDateTime

class Film(
    val id: Int? = null,
    val title: String,
    val description: String?,
    val releaseYear: Int?,
    val rentalDuration: Short,
    val rentalRate: BigDecimal,
    val length: Short?,
    val replacementCost: BigDecimal,
    val language: Language,
    val actors: List<Actor>,
    val categories: List<Category>
) {
    constructor(entity: FilmEntity, language: Language, actors: List<Actor>, categories: List<Category>): this(
        id = entity.filmId,
        title = entity.title,
        description = entity.description,
        releaseYear = entity.releaseYear,
        rentalDuration = entity.rentalDuration,
        rentalRate = entity.rentalRate,
        length = entity.length,
        replacementCost = entity.replacementCost,
        language = language,
        actors = actors,
        categories = categories
    )

    constructor(command: FilmCreateCommand, language: Language, actors: List<Actor>, categories: List<Category>): this(
        title = command.title,
        description = command.description,
        releaseYear = command.releaseYear,
        rentalDuration = command.rentalDuration,
        rentalRate = command.rentalRate,
        length = command.length,
        replacementCost = command.replacementCost,
        language = language,
        actors = actors,
        categories = categories
    )

    fun toEntity(): FilmEntity {
        return FilmEntity().also { entity ->
            entity.filmId = id
            entity.title = title
            entity.description = description
            entity.releaseYear = releaseYear
            entity.rentalDuration = rentalDuration
            entity.rentalRate = rentalRate
            entity.length = length
            entity.replacementCost = replacementCost
            entity.languageId = language.id
            entity.lastUpdate = LocalDateTime.now()
        }
    }


    companion object {
        fun createByFilmWithRelationEntities(entities: List<FilmWithRelationEntity>): List<Film> {
            return entities
                    .groupBy { it.filmId }
                    .values
                    .map { filmEntities ->
                        val filmEntity = filmEntities.first()

                        val language = Language(
                            id = filmEntity.languageId,
                            name = filmEntity.languageName,
                            updatedAt = filmEntity.languageLastUpdate
                        )

                        val actors = if (filmEntities.all { it.actorId == null }) {
                            emptyList()
                        } else {
                            filmEntities.distinctBy { it.actorId }.map {
                                Actor(
                                    id = it.actorId,
                                    firstName = it.actorFirstName,
                                    lastName = it.actorLastName,
                                    updatedAt = it.actorLastUpdate
                                )
                            }
                        }

                        val categories = if (filmEntities.all { it.categoryId == null }) {
                            emptyList()
                        } else {
                            filmEntities.distinctBy { it.categoryId }.map {
                                Category(
                                    id = it.categoryId,
                                    name = it.categoryName,
                                    updatedAt = it.categoryLastUpdate
                                )
                            }
                        }

                        Film(entity = filmEntity, language = language, actors = actors, categories = categories)
                    }
        }

    }
}