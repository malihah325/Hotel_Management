package com.example.hotelmanagement.repositories;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotelmanagement.entity.Room;

public interface RoomRepo extends JpaRepository<Room, Long>{

}
