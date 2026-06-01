CREATE TABLE IF NOT EXISTS stalls (
  id BIGINT NOT NULL,
  name VARCHAR(80) NOT NULL,
  type VARCHAR(80) NOT NULL,
  pos_x INT NOT NULL,
  pos_y INT NOT NULL,
  serve_speed DOUBLE NOT NULL,
  distance INT NOT NULL,
  avg_prep DOUBLE NOT NULL,
  rating DOUBLE NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL,
  openid VARCHAR(120) NOT NULL,
  nickname VARCHAR(80) NOT NULL,
  class_end_time VARCHAR(5),
  report_count INT NOT NULL DEFAULT 0,
  time_saved INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_users_openid UNIQUE (openid)
);

CREATE TABLE IF NOT EXISTS auth_tokens (
  token VARCHAR(80) NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (token),
  INDEX idx_auth_tokens_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS queue_snapshots (
  id BIGINT NOT NULL,
  stall_id BIGINT NOT NULL,
  queue_count INT NOT NULL,
  reporter_id BIGINT,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_queue_snapshots_stall_created (stall_id, created_at, id)
);

CREATE TABLE IF NOT EXISTS feedback_records (
  id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  stall_id BIGINT NOT NULL,
  accurate BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_feedback_records_user_id (user_id),
  INDEX idx_feedback_records_stall_id (stall_id)
);

INSERT INTO stalls (id, name, type, pos_x, pos_y, serve_speed, distance, avg_prep, rating) VALUES
  (1, '快餐米饭', '盖浇饭/小炒', 22, 34, 2.3, 128, 2.5, 4.4),
  (2, '面食窗口', '汤面/拌面', 36, 28, 1.7, 102, 3.5, 4.6),
  (3, '轻食套餐', '沙拉/鸡胸', 48, 42, 1.5, 96, 2.2, 4.1),
  (4, '麻辣香锅', '自选称重', 64, 30, 1.1, 168, 7.0, 4.8),
  (5, '包子粥铺', '包子/粥', 16, 18, 2.9, 54, 1.3, 4.0),
  (6, '预制套餐', '固定套餐', 76, 24, 3.2, 88, 1.8, 4.2);

INSERT INTO queue_snapshots (id, stall_id, queue_count, reporter_id, created_at) VALUES
  (1, 1, 8, NULL, '2026-05-01 12:00:00'),
  (2, 2, 9, NULL, '2026-05-01 12:00:00'),
  (3, 3, 5, NULL, '2026-05-01 12:00:00'),
  (4, 4, 13, NULL, '2026-05-01 12:00:00'),
  (5, 5, 6, NULL, '2026-05-01 12:00:00'),
  (6, 6, 10, NULL, '2026-05-01 12:00:00');
