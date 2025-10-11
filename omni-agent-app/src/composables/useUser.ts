// composables/useUser.ts

export interface User {
    userId: string
    username: string
    role?: string
    token?: string
}

class UserStore {
    private user: User

    constructor() {
        // 默认用户信息
        this.user = {
            userId: 'default-001',
            username: '默认用户',
            role: 'user',
            token: ''
        }
    }

    // 获取用户信息
    getUser(): User {
        return this.user
    }

    // 获取 userId
    getUserId(): string {
        return this.user.userId
    }

    // 设置用户信息
    setUser(user: User) {
        this.user = user
    }

    // 判断是否已登录
    isLoggedIn(): boolean {
        return !!this.user.userId
    }

    // 清除用户信息（登出）
    clear() {
        this.user = {
            userId: '',
            username: '',
            role: '',
            token: ''
        }
    }
}

// 单例
export const userStore = new UserStore()
