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

    private String filterAction;
    private Integer filterActionId;

    private String addedBy;

    private Double addedAt;


}
