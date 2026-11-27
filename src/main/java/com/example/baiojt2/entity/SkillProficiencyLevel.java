package com.example.baiojt2.entity;

public enum SkillProficiencyLevel {
    BEGINNER(1, "Mới bắt đầu", "Biết khái niệm cơ bản, cần hướng dẫn khi thực hiện."),
    BASIC(2, "Cơ bản", "Có thể thực hiện tác vụ đơn giản với tài liệu hoặc hướng dẫn."),
    INTERMEDIATE(3, "Trung bình", "Có thể tự thực hiện công việc thông thường và xử lý vấn đề phổ biến."),
    ADVANCED(4, "Nâng cao", "Làm việc độc lập với tình huống phức tạp, có thể hỗ trợ người khác."),
    EXPERT(5, "Thành thạo", "Có kiến thức chuyên sâu, thiết kế giải pháp và hướng dẫn/chia sẻ kinh nghiệm.");

    private final int value;
    private final String label;
    private final String description;

    SkillProficiencyLevel(int value, String label, String description) {
        this.value = value;
        this.label = label;
        this.description = description;
    }

    public int getValue() { return value; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }

    public static SkillProficiencyLevel fromValue(int value) {
        for (SkillProficiencyLevel level : values()) {
            if (level.value == value) return level;
        }
        throw new IllegalArgumentException("Mức kỹ năng phải từ 1 đến 5.");
    }
}
