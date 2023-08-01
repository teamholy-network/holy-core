package de.teamholy.core.bungee.model;

import eu.koboo.en2do.repository.entity.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChatFilter {

    @Id
    private String chatFilterId;

    private String word;

    private Integer severity;

    private String addedBy;

    private Double addedAt;



}
