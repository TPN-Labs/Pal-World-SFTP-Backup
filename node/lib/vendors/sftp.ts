import ClientFtp from 'ssh2-sftp-client';

export class SftpClient {
    private sftp: ClientFtp;
    private readonly host: string;
    private readonly user: string;
    private readonly pass: string;
    private readonly port: number;

    constructor(host: string, user: string, pass: string, port: number) {
        this.sftp = new ClientFtp();
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.port = port;
    }

    async connect(): Promise<void> {
        await this.sftp.connect({
            host: this.host,
            username: this.user,
            password: this.pass,
            port: this.port,
        });
    }

    async downloadFolder(remotePath: string, localPath: string): Promise<void> {
        await this.sftp.downloadDir(remotePath, localPath);
    }

    async close(): Promise<void> {
        await this.sftp.end();
    }
}
