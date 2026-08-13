package cr.ac.cuc.ejemplonativop5.data.remote

import cr.ac.cuc.ejemplonativop5.data.remote.dto.PersonaDto

/** Representa la respuesta paginada devuelta por el microservicio de personas. */
data class PagedPersonDto(val items: List<PersonaDto>, val pageNumber: Int, val pageSize: Int, val totalCount: Int, val totalPages: Int)
