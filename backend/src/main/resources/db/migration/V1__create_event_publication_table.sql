-- https://docs.spring.io/spring-modulith/reference/appendix.html#schemas.mysql
CREATE TABLE IF NOT EXISTS event_publication
(
  id               BINARY(16) NOT NULL, -- https://github.com/spring-projects/spring-modulith/issues/94#issue-1510214757
  listener_id      VARCHAR(512) NOT NULL,
  event_type       VARCHAR(512) NOT NULL,
  serialized_event VARCHAR(4000) NOT NULL,
  publication_date TIMESTAMP(6) NOT NULL,
  completion_date  TIMESTAMP(6) DEFAULT NULL NULL,
  PRIMARY KEY (id),
  INDEX event_publication_by_completion_date_idx (completion_date)
);