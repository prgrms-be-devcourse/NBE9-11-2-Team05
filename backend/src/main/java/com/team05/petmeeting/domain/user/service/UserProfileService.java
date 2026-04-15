package com.team05.petmeeting.domain.user.service;

import com.team05.petmeeting.domain.cheer.repository.CheerRepository;
import com.team05.petmeeting.domain.feed.repository.FeedRepository;
import com.team05.petmeeting.domain.user.dto.profile.MyProfileDetailRes;
import com.team05.petmeeting.domain.user.dto.profile.UserCheerAnimalRes;
import com.team05.petmeeting.domain.user.dto.profile.UserFeedRes;
import com.team05.petmeeting.domain.user.dto.profile.UserProfileRes;
import com.team05.petmeeting.domain.user.entity.User;
import com.team05.petmeeting.domain.user.errorCode.UserErrorCode;
import com.team05.petmeeting.domain.user.repository.UserRepository;
import com.team05.petmeeting.global.exception.BusinessException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FeedRepository feedRepository;
    private final CheerRepository cheerRepository;

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }

    public UserProfileRes modifyProfileImageUrl(Long userId, String profileImageUrl) {
        User user = getUserById(userId);
        user.updateProfileImageUrl(profileImageUrl);
        return UserProfileRes.from(user);
    }

    public UserProfileRes modifyNickname(Long userId, String nickname) {
        User user = getUserById(userId);
        user.updateNickname(nickname);
        return UserProfileRes.from(user);
    }

    public void modifyPassword(Long userId, @NotBlank(message = "현재 비밀번호를 입력해주세요.") String currentPassword,
                               @NotBlank(message = "새 비밀번호를 입력해주세요.") String newPassword) {
        User user = getUserById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_PASSWORD);
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException(UserErrorCode.SAME_AS_OLD_PASSWORD);
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);

    }

    public UserProfileRes modifyUsername(Long userId, String newUsername) {
        User user = getUserById(userId);
        user.updateUsername(newUsername);
        return UserProfileRes.from(user);
    }

    public MyProfileDetailRes getMyProfileDetails(Long userId) {
        User user = getUserById(userId);
        Long feedCount = feedRepository.countByUser(user);
        Long cheerCount = cheerRepository.countDistinctAnimalByUser(user);
        return MyProfileDetailRes.from(feedCount, cheerCount);
    }

    public UserFeedRes getMyFeeds(Long userId) {
        User user = getUserById(userId);
        return UserFeedRes.from(feedRepository.findAllByUserOrderByCreatedAtDesc(user));
    }

    public UserCheerAnimalRes getMyCheerAnimals(Long userId) {
        User user = getUserById(userId);
        List<Object[]> animalCountMap = cheerRepository.findCheerCountsByUser(user);
        return UserCheerAnimalRes.from(animalCountMap);
    }

    public UserProfileRes getUserProfile(Long userId) {
        User user = getUserById(userId);
        return UserProfileRes.from(user);
    }
}
