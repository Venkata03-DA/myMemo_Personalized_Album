package com.venkata.mymemo.entity;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "albums")
public class Album {

	@Id // Creating a unique identifier for each album
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generating the ID value
	private Long id; // Unique identifier for the album

	@Column(nullable = false)
	private String title; // Title of the album

	@Column(name = "cover_image_url")
	private String coverImageUrl; // URL of the album's cover image

	@Column(name = "event_date")
	private LocalDate eventDate; // Date of the event associated with the album

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt; // Timestamp for when the album was created

	@PrePersist // Method to set default values before saving the album to the database
	void onCreate() {
    	if (createdAt == null) { // Set createdAt to the current time if it's not already set
        	createdAt = Instant.now();
    }
    	if (title == null || title.trim().isEmpty()) { // Set title to an empty string if it's null or empty
    		title = "";
		}

    }


	public Long getId() { // Getter method for the album's unique identifier
		return id;
	}

	public void setId(Long id) { 
		this.id = id;
	}

	public String getTitle() { 	// Getter method for the album's title
		return title; // Return the title of the album
	}

	public void setTitle(String title) { // Setter method for the album's title
		this.title = title; // Set the title of the album
	}

	public String getCoverImageUrl() { // Getter method for the album's cover image URL
		return coverImageUrl; // Return the cover image URL of the album
	}

	public void setCoverImageUrl(String coverImageUrl) { // Setter method for the album's cover image URL
		this.coverImageUrl = coverImageUrl; // Set the cover image URL of the album
	}

	public LocalDate getEventDate() { //	 Getter method for the album's event date
		return eventDate; // Return the event date associated with the album
	}

	public void setEventDate(LocalDate eventDate) { // Setter method for the album's event date
		this.eventDate = eventDate; // Set the event date associated with the album
	}

	public Instant getCreatedAt() { // Getter method for the album's creation timestamp
		return createdAt; // Return the timestamp for when the album was created
	}

	public void setCreatedAt(Instant createdAt) { //Setter method for the album's creation timestamp
		this.createdAt = createdAt; // Set the timestamp for when the album was created
	} //Need not create a setter for createdAt as it is set automatically when the album is created, but it's included here for completeness and potential future use.
}
