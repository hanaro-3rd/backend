package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAnyAttribute;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification<T> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "NOTIFICATION_ID")
	private int id;

	@Column
	private String type;

	@Column
	private String sender;

	@Column
	private String channelId;

	@Column
	private String data;

	@Column
	@CreationTimestamp
	private final LocalDateTime createdAt = LocalDateTime.now();

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void newConnect() {
		this.type = "new";
	}

	public void closeConnect() {
		this.type = "close";
	}
}