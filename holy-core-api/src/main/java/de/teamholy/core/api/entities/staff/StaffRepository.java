package de.teamholy.core.api.entities.staff;

import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.List;
import java.util.UUID;

@Collection("staff_profile_collection")
public interface StaffRepository extends Repository<StaffProfile, UUID> {

}
